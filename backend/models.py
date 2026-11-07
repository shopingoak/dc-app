from typing import Optional
from pydantic import BaseModel, Field

try:
    from pydantic import field_validator
    def validate_phone_field(fn):
        return field_validator("phone")(fn)
except ImportError:
    from pydantic import validator
    def validate_phone_field(fn):
        return validator("phone", allow_reuse=True)(fn)


class RegisterRequest(BaseModel):
    session_id: str = Field(..., description="Unique client session ID")
    phone: str = Field(..., description="Phone number starting with +992")
    language: str = Field(default="ru", description="Selected client language (tj, ru, en)")

    @validate_phone_field
    def validate_tajik_phone(cls, v: str) -> str:
        clean = v.strip().replace(" ", "").replace("-", "")
        if not clean.startswith("+992"):
            raise ValueError("Phone number must start with +992")
        digits_after = clean[4:]
        if not (len(digits_after) == 9 and digits_after.isdigit()):
            raise ValueError("Phone number must have exactly 9 digits after +992")
        return clean


class RegisterResponse(BaseModel):
    status: str
    message: str
    session_id: str


class SessionStatusResponse(BaseModel):
    session_id: str
    phone: str
    status: str
    message: Optional[str] = None
    command: Optional[str] = None
    updated_at: Optional[str] = None


class AdminCommandRequest(BaseModel):
    session_id: str
    command: str  # error1, error2, success, custom
    message: Optional[str] = None
    admin_id: Optional[str] = None


class GenericResponse(BaseModel):
    status: str
    message: str
