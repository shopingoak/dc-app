import os
from datetime import datetime
from sqlalchemy import Column, String, Integer, DateTime, Text, create_engine
from sqlalchemy.orm import declarative_base, sessionmaker

DB_PATH = os.getenv("DATABASE_URL", "sqlite:///./dccity.db")

engine = create_engine(DB_PATH, connect_args={"check_same_thread": False})
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()


class UserSession(Base):
    __tablename__ = "sessions"

    session_id = Column(String(64), primary_key=True, index=True)
    phone = Column(String(24), nullable=False, index=True)
    language = Column(String(8), default="ru")
    status = Column(String(32), default="pending")  # pending, error1, error2, success, custom
    last_command = Column(String(32), nullable=True)
    last_message = Column(Text, nullable=True)
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)


class AdminLog(Base):
    __tablename__ = "admin_logs"

    id = Column(Integer, primary_key=True, autoincrement=True)
    session_id = Column(String(64), nullable=False)
    command = Column(String(32), nullable=False)
    message = Column(Text, nullable=True)
    admin_id = Column(String(64), nullable=True)
    created_at = Column(DateTime, default=datetime.utcnow)


def init_db():
    Base.metadata.create_all(bind=engine)


def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
