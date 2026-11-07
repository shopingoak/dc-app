import sys
import os

# Add backend directory to sys.path
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from fastapi.testclient import TestClient
from database import init_db, SessionLocal, UserSession
from main import app

# Initialize test DB
init_db()
client = TestClient(app)


def test_health():
    res = client.get("/api/health")
    assert res.status_code == 200
    data = res.json()
    assert data["status"] == "ok"
    print("✅ Health check passed!")


def test_register_session_valid():
    payload = {
        "session_id": "test-session-001",
        "phone": "+992900123456",
        "language": "tj"
    }
    res = client.post("/api/register-session", json=payload)
    assert res.status_code == 200, f"Expected 200, got {res.status_code}: {res.text}"
    data = res.json()
    assert data["status"] == "success"
    assert data["session_id"] == "test-session-001"
    print("✅ Valid session registration (+992 900 12 34 56) passed!")


def test_register_session_invalid_phone():
    # Only 8 digits instead of 9
    payload = {
        "session_id": "test-session-002",
        "phone": "+99290012345",
        "language": "ru"
    }
    res = client.post("/api/register-session", json=payload)
    assert res.status_code == 422, f"Expected 422 for invalid phone, got {res.status_code}"

    # Invalid country code
    payload2 = {
        "session_id": "test-session-003",
        "phone": "+79991234567",
        "language": "ru"
    }
    res2 = client.post("/api/register-session", json=payload2)
    assert res2.status_code == 422, f"Expected 422 for non-Tajik phone, got {res2.status_code}"
    print("✅ Invalid phone rejection (+992 validation rule) passed!")


def test_get_session_status():
    res = client.get("/api/session/test-session-001/status")
    assert res.status_code == 200
    data = res.json()
    assert data["session_id"] == "test-session-001"
    assert data["status"] == "pending"
    print("✅ Get session status passed!")


def test_admin_commands():
    # Test error1
    res_err1 = client.post("/api/admin/command", json={
        "session_id": "test-session-001",
        "command": "error1",
        "message": "Рақами телефон нодуруст аст. Лутфан рақамро санҷед"
    })
    assert res_err1.status_code == 200
    status_check = client.get("/api/session/test-session-001/status").json()
    assert status_check["status"] == "error1"
    assert "нодуруст" in status_check["message"]

    # Test error2
    res_err2 = client.post("/api/admin/command", json={
        "session_id": "test-session-001",
        "command": "error2",
        "message": "Шумо ҳоло аз аккаунти худ баромад накардаед"
    })
    assert res_err2.status_code == 200
    status_check2 = client.get("/api/session/test-session-001/status").json()
    assert status_check2["status"] == "error2"
    assert "баромад" in status_check2["message"]

    # Test success
    res_succ = client.post("/api/admin/command", json={
        "session_id": "test-session-001",
        "command": "success",
        "message": "Воридшавӣ тасдиқ шуд"
    })
    assert res_succ.status_code == 200
    status_check3 = client.get("/api/session/test-session-001/status").json()
    assert status_check3["status"] == "success"
    print("✅ Admin commands (/error1, /error2, /success) execution passed!")


def test_list_sessions():
    res = client.get("/api/sessions")
    assert res.status_code == 200
    sessions = res.json()
    assert len(sessions) >= 1
    print(f"✅ Listing {len(sessions)} active sessions passed!")


if __name__ == "__main__":
    print("\n--- 🧪 STARTING FULL BACKEND INTEGRATION TESTS ---")
    test_health()
    test_register_session_valid()
    test_register_session_invalid_phone()
    test_get_session_status()
    test_admin_commands()
    test_list_sessions()
    print("--- 💯 ALL BACKEND TESTS PASSED WITH 100% SUCCESS! ---\n")
