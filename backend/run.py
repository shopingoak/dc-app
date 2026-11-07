import os
import asyncio
import uvicorn
from dotenv import load_dotenv

load_dotenv()

from main import app, set_bot_notifier
import bot


async def run_fastapi(host: str, port: int):
    config = uvicorn.Config(app, host=host, port=port, log_level="info")
    server = uvicorn.Server(config)
    print(f"🚀 FastAPI server running on http://{host}:{port}")
    await server.serve()


async def main():
    host = os.getenv("HOST", "0.0.0.0")
    port = int(os.getenv("PORT", 8000))

    # Initialize Telegram Bot instance and link callback
    tg_bot = bot.init_telegram_bot()
    set_bot_notifier(bot.notify_admin_new_session)

    tasks = [run_fastapi(host, port)]

    # Start bot if token provided
    if tg_bot:
        print("🤖 Telegram Bot is configured. Starting aiogram polling...")
        tasks.append(bot.start_telegram_bot())
    else:
        print("⚠️ BOT_TOKEN is empty in .env. Bot polling skipped. (FastAPI running normally)")

    await asyncio.gather(*tasks)


if __name__ == "__main__":
    try:
        asyncio.run(main())
    except (KeyboardInterrupt, SystemExit):
        print("\n👋 Server shut down gracefully.")
