# LifeCycle – Smart Personal Asset & Product Lifecycle Management

Own smarter. Maintain better. Waste less.

Java Full Stack: **Spring Boot 3 (Java 17) + Spring Security/JWT + JPA** backend, **React + Vite + Tailwind** frontend.

## Run it

**Backend** (needs Java 17+ and Maven)
```bash
cd lifecycle-backend
mvn spring-boot:run            # starts on :8080 with in-memory H2, zero setup
```
MySQL instead of H2:
```bash
export DB_USER=root DB_PASSWORD=yourpassword JWT_SECRET=<32+ random characters>
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

**Frontend** (needs Node 18+)
```bash
cd lifecycle-frontend
npm install
npm run dev                    # http://localhost:5173 (proxies /api to :8080)
```

**Sample account** (H2 mode only): `demo@lifecycle.app` / `demo1234`. Its products are named "Sample ..." and are demo data, not real records.

## Structure
```
lifecycle-backend/src/main/java/com/lifecycle/
  config/       SecurityConfig (JWT, CORS, BCrypt), DataSeeder (sample data)
  controller/   Auth, Asset, Warranty, Maintenance, ServiceExpense, Dashboard, Notification, Document
  service/      AssetService (ownership checks, costs), ValuationService (depreciation + repair-vs-replace),
                MaintenanceService, NotificationService, DashboardService, InvoiceExtractionService
  entity/       User, Asset, Warranty, MaintenanceSchedule, ServiceRecord, Expense, Notification
  repository/   Spring Data JPA repositories
  dto/          Request/response records
  security/     JwtUtil, JwtFilter
  scheduler/    ReminderScheduler (daily 08:00)
  exception/    GlobalExceptionHandler
  utility/      CurrentUser
lifecycle-frontend/src/
  pages/        Login, Register, Dashboard, Assets, AddAsset, AssetDetails, Maintenance, Notifications
  components/   Layout (sidebar)
  context/      AuthContext
  services/     api.js (axios + helpers)
```

## API (base `/api`, JWT required except `/auth/*`)
| Method | Endpoint | Purpose |
|---|---|---|
| POST | /auth/register, /auth/login | Account + token |
| GET | /users/profile | Profile |
| GET/POST | /assets | List (`?q=` search) / create |
| GET/PUT/DELETE | /assets/{id} | Details / update / delete (cascades) |
| GET | /assets/{id}/valuation, /costs | Depreciation, total ownership cost |
| GET | /assets/{id}/recommendation?repairCost= | Repair vs replace |
| POST | /warranties | Add warranty (expiry auto-calculated from months) |
| GET | /assets/{id}/warranty, /warranties/expiring | Warranty views |
| POST | /maintenance, /maintenance/{id}/complete | Schedule / mark done (next date recalculated) |
| GET | /maintenance/upcoming, /assets/{id}/maintenance, /assets/{id}/maintenance/suggestions | Maintenance views |
| POST | /services, /expenses | Repair and expense records |
| GET | /assets/{id}/services, /assets/{id}/expenses | History |
| POST | /documents/extract | Parse invoice text into suggested fields |
| GET | /dashboard/summary | Dashboard statistics |
| GET/POST | /notifications, /notifications/{id}/read | In-app reminders |

## Design notes
- Every child-record endpoint verifies the asset belongs to the logged-in user (`AssetService.owned`).
- Depreciation rules are configurable in `application.properties` (`app.depreciation.*`).
- Suggested maintenance tasks are labelled "General recommendation", separate from user/manufacturer schedules.
- The recommendation is decision support, not a guarantee, and says so in the response.

## Not included yet (next phases)
Real OCR (plug Tess4J/Tesseract in front of `InvoiceExtractionService`), file upload/document vault, PDF reports (PDFBox),
QR codes (ZXing), family groups and sharing, ownership transfer, email/SMS delivery, forgot-password, product photos.
