# Дастури пурраи насб ва истифодабарии DC City (Android + Python + Telegram)

## Мундариҷа
1. [Муаррифӣ ва сохтори лоиҳа](#сохтори-лоиҳа)
2. [Талаботҳои системавӣ](#талаботҳо)
3. [Насб ва танзими Python Backend & Telegram Bot](#насби-backend)
4. [Санҷиши замимаи Android (APK)](#санҷиши-android)
5. [Фармонҳои Telegram Bot барои админ](#фармонҳои-бот)
6. [Санҷиши фаврӣ бидуни сервер (Offline Simulation)](#санҷиши-фаврӣ)

---

## 1. Сохтори лоиҳа
```text
/
├── app/                      # Коди пурраи Android (Kotlin + Jetpack Compose)
│   ├── src/main/java/com/example/
│   │   ├── MainActivity.kt
│   │   ├── model/Models.kt
│   │   ├── api/DcCityApiService.kt
│   │   ├── repository/DcCityRepository.kt
│   │   ├── ui/
│   │   │   ├── theme/
│   │   │   ├── components/
│   │   │   ├── screens/
│   │   │   │   ├── OnboardingScreen.kt
│   │   │   │   ├── LoginScreen.kt
│   │   │   │   ├── VerificationWaitScreen.kt
│   │   │   │   └── DashboardScreen.kt
│   │   │   └── dialogs/
│   │   └── res/drawables/    # Нишонаҳо, парчами Тоҷикистон, логотип
├── backend/                  # Сервери Python FastAPI + Telegram Bot
│   ├── main.py               # REST API endpoints & Webhooks
│   ├── bot.py                # aiogram 3 Telegram Bot барои админ
│   ├── database.py           # SQLite database models
│   ├── models.py             # Pydantic schemas бо санҷиши рақам
│   ├── run.py                # Якҷоя ба кор андохтани API ва Bot
│   ├── requirements.txt      # Китобхонаҳои зарурӣ
│   ├── .env.example          # Намунаи калидҳо ва токенҳо
│   └── README.md
├── metadata.json
└── README.md
```

---

## 2. Насби Backend ва Telegram Bot

### 1) Насб кардани китобхонаҳо:
Дар терминали худ фармонҳои зеринро иҷро намоед:
```bash
cd backend
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
```

### 2) Танзими `.env`:
Файли `.env.example`-ро ба `.env` нусхабардорӣ карда, маълумотро ворид кунед:
```env
BOT_TOKEN=1234567890:ABCdef...   # Аз @BotFather
ADMIN_ID=123456789                # ID-и Telegram-и худатон аз @userinfobot
HOST=0.0.0.0
PORT=8000
DATABASE_URL=sqlite:///./dccity.db
```

### 3) Ба кор андохтан:
```bash
python run.py
```

---

## 3. Фармонҳои Telegram Bot
- `/admin` — кушодани панели идоракунӣ бо рӯйхати ҳамаи корбарони пайвастшуда.
- `/error1` — ба корбар фиристодани паёми:
  «Рақами телефон нодуруст аст. Лутфан рақамро санҷед»
- `/error2` — ба корбар фиристодани паёми:
  «Шумо ҳоло аз аккаунти худ баромад накардаед»
- `/success` — тасдиқи воридшавӣ ва кушодани дастрасӣ ба ҳамёни DC City.

Ҳамчунин тугмаҳои фаврии инлайн (Inline Buttons) мустақиман дар огоҳиномаи ҳар як дархост фиристода мешаванд.

---

## 4. Санҷиши фаврӣ дар худи замима (Simulation)
Барои роҳати санҷиш ва намоиш, дар худи замимаи Android (дар экрани интизорӣ) тугмаҳои санҷиши фаврӣ гузошта шудаанд:
- `[ /error1 ]` — фавран хатогии рақамро нишон медиҳад.
- `[ /error2 ]` — фавран огоҳии аз аккаунт набаромаданро нишон медиҳад.
- `[ /success ]` — фавран воридшавиро тасдиқ карда, корбарро ба экрани асосии бонк мегузаронад.
Ин имкон медиҳад, ки ҳатто бидуни пайваст ба сервер тамоми реаксияҳои UI санҷида шаванд!
