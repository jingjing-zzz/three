curl -s -X POST http://localhost:8080/admin-api/system/auth/login \
  -H 'Content-Type: application/json' \
  -H 'tenant-id: 1' \
  -d '{"username":"admin","password":"admin123"}'