# Rabam Full Stack Case Projesi

Bu proje, araç servis süreçlerini yönetmek için geliştirilmiş full stack bir web uygulamasıdır.

Uygulama; Spring Boot tabanlı bir backend, React + TypeScript tabanlı bir frontend, MySQL veritabanı, RabbitMQ event sistemi, Docker desteği, çoklu dil desteği, dashboard özetleri ve test yapısını içermektedir.

---

## Proje Özellikleri

### Backend Özellikleri

- Spring Boot REST API
- Katmanlı mimari
- Araç CRUD işlemleri
- Servis CRUD işlemleri
- Servis durum geçiş kontrolü
- Optimistic locking
- Pessimistic locking
- Araç başına maksimum aktif servis kuralı
- RabbitMQ ile domain event yayınlama
- Audit event consumer
- MySQL veritabanı desteği
- DTO tabanlı request / response yapısı
- Mapper katmanı
- Validation desteği
- Global exception handling
- Türkçe / İngilizce hata mesajı desteği
- Swagger / OpenAPI dokümantasyonu
- Unit test ve integration test desteği

### Frontend Özellikleri

- React + TypeScript
- Vite
- Material UI
- Araç yönetim sayfası
- Servis yönetim sayfası
- Dashboard özet kartları
- Autocomplete ile araç seçimi
- Araca ve servis durumuna göre filtreleme
- Dark / light tema desteği
- Türkçe / İngilizce dil seçimi
- Axios API client
- Nginx ile Docker üzerinde frontend yayınlama

---

## Kullanılan Teknolojiler

### Backend

- Java 17
- Spring Boot 4.1.0
- Spring Web
- Spring Data JPA
- MySQL
- RabbitMQ
- Hibernate
- Lombok
- Swagger / OpenAPI
- JUnit 5
- Mockito
- Maven

### Frontend

- React
- TypeScript
- Vite
- Material UI
- Axios
- i18next
- react-i18next
- Nginx

### DevOps

- Docker
- Docker Compose

---

## Proje Klasör Yapısı

```text
rabam/
├── src/
│   ├── main/
│   │   ├── java/com/munevver/rabam/
│   │   │   ├── car/
│   │   │   ├── service/
│   │   │   ├── dashboard/
│   │   │   ├── event/
│   │   │   ├── common/
│   │   │   └── config/
│   │   └── resources/
│   └── test/
├── frontend/
│   ├── src/
│   │   ├── api/
│   │   ├── pages/
│   │   ├── components/
│   │   ├── i18n/
│   │   └── types/
│   ├── Dockerfile
│   └── nginx.conf
├── docs/
│   └── screenshots/
├── Dockerfile
├── docker-compose.yml
└── README.md
```

---

## Mimari Yapı

Backend tarafında katmanlı mimari kullanılmıştır.

```text
Controller → Service → Repository
              ↓
            Mapper
```

### Katmanların Sorumlulukları

| Katman | Sorumluluk |
|---|---|
| Controller | HTTP isteklerini karşılar ve response döner |
| Service | İş kurallarını ve transaction yönetimini içerir |
| Repository | Veritabanı işlemlerini gerçekleştirir |
| Mapper | Entity ve DTO dönüşümlerini yapar |
| DTO | Request ve response modellerini temsil eder |
| Exception Handler | Hataları merkezi ve standart şekilde yönetir |

---

## İş Kuralları

Servis modülünde aşağıdaki iş kuralları uygulanmıştır:

```text
Bir servis ilk oluşturulduğunda varsayılan olarak PENDING durumunda olur.

İzin verilen durum geçişleri:
PENDING → IN_PROGRESS
IN_PROGRESS → DONE

DONE son durumdur. DONE durumundan sonra başka bir geçiş yapılamaz.

Bir araç için aynı anda en fazla 2 adet IN_PROGRESS servis bulunabilir.

Servis güncelleme işlemlerinde optimistic locking uygulanır.

Aktif servis limiti kontrol edilirken pessimistic locking kullanılır.
```

---

## Ekran Görüntüleri

Ekran görüntülerini aşağıdaki klasöre ekleyebilirsin:

```text
docs/screenshots/
```

Önerilen dosya isimleri:

```text
docs/screenshots/cars-page.png
docs/screenshots/cars-page-2.png
docs/screenshots/services-page.png
docs/screenshots/services-page-2.png


```

### Araçlar Sayfası

![Araçlar Sayfası](docs/screenshots/cars-page.png)

### Servisler Sayfası

![Servisler Sayfası](docs/screenshots/services-page.png)



---

## Docker ile Çalıştırma

Projeyi çalıştırmanın en kolay yolu Docker Compose kullanmaktır.

### Tüm servisleri başlatma

```bash
docker compose up -d --build
```

Bu komut aşağıdaki servisleri ayağa kaldırır:

```text
MySQL
RabbitMQ
Spring Boot Backend
React Frontend
```

### Tüm servisleri durdurma

```bash
docker compose down
```

### Servisleri durdurup volume verilerini silme

```bash
docker compose down -v
```

> Dikkat: Bu komut veritabanındaki kayıtları da siler.

---

## Uygulama Adresleri

| Servis | URL |
|---|---|
| Frontend | http://localhost:5173 |
| Backend Swagger | http://localhost:8080/swagger-ui.html |
| Dashboard Summary API | http://localhost:8080/api/dashboard/summary |
| RabbitMQ Management | http://localhost:15683 |

RabbitMQ giriş bilgileri:

```text
username: rabam
password: rabam
```

---

## Manuel Çalıştırma

Docker ile tüm sistemi çalıştırmak yerine backend ve frontend ayrı ayrı da çalıştırılabilir.

### MySQL ve RabbitMQ başlatma

```bash
docker compose up -d mysql rabbitmq
```

### Backend çalıştırma

Proje ana dizininde:

```bash
mvn spring-boot:run
```

Backend şu adreste çalışır:

```text
http://localhost:8080
```

### Frontend çalıştırma

```bash
cd frontend
npm install
npm run dev
```

Frontend şu adreste çalışır:

```text
http://localhost:5173
```

---

## API Endpointleri

### Araç API

| Method | Endpoint | Açıklama |
|---|---|---|
| GET | `/api/cars` | Araçları sayfalı şekilde listeler |
| POST | `/api/cars` | Yeni araç oluşturur |
| PUT | `/api/cars/{id}` | Mevcut aracı günceller |

### Servis API

| Method | Endpoint | Açıklama |
|---|---|---|
| GET | `/api/services` | Servisleri filtreli ve sayfalı şekilde listeler |
| GET | `/api/services/{id}` | ID değerine göre servis getirir |
| POST | `/api/services` | Yeni servis oluşturur |
| PUT | `/api/services/{id}` | Servis bilgilerini veya durumunu günceller |

### Dashboard API

| Method | Endpoint | Açıklama |
|---|---|---|
| GET | `/api/dashboard/summary` | Dashboard için özet verileri getirir |

---

## Örnek Requestler

### Araç Oluşturma

```json
{
  "licensePlate": "34 ABC 123",
  "brand": "Toyota",
  "model": "Corolla"
}
```

### Servis Oluşturma

```json
{
  "title": "Yağ Değişimi",
  "description": "Motor yağı ve yağ filtresi değiştirilecek",
  "carId": 1
}
```

### Servis Durumu Güncelleme

```json
{
  "status": "IN_PROGRESS",
  "version": 0
}
```

---

## Çoklu Dil Desteği

Projede Türkçe ve İngilizce dil desteği bulunmaktadır.

### Frontend Dil Desteği

Frontend arayüzünde dil seçici üzerinden dil değiştirilebilir.

Desteklenen diller:

```text
TR
EN
```

### Backend Dil Desteği

Backend hata ve bilgilendirme mesajları `Accept-Language` header değerine göre döner.

Örnek:

```http
Accept-Language: tr
```

```http
Accept-Language: en
```

---

## RabbitMQ Event Akışı

Backend tarafında araç veya servis kayıtları oluşturulduğunda ya da güncellendiğinde domain event yayınlanır.

Örnek event tipleri:

```text
CAR_CREATED
CAR_UPDATED
SERVICE_CREATED
SERVICE_UPDATED
```

Bu eventler RabbitMQ exchange üzerinden ilgili kuyruğa gönderilir ve audit consumer tarafından tüketilir.

RabbitMQ yönetim paneli:

```text
http://localhost:15683
```

---

## Testler

### Backend testlerini çalıştırma

```bash
mvn test
```

Projede aşağıdaki test türleri bulunmaktadır:

```text
Unit testler
Integration testler
Optimistic locking testi
Pessimistic locking / max active service concurrency testi
```

### Frontend build testi

```bash
cd frontend
npm run build
```

---

## Logları Görüntüleme

### Tüm Docker logları

```bash
docker compose logs -f
```

### Backend logları

```bash
docker logs -f rabam-backend
```

### Frontend logları

```bash
docker logs -f rabam-frontend
```

### RabbitMQ logları

```bash
docker logs -f rabam-rabbitmq
```

### MySQL logları

```bash
docker logs -f rabam-mysql
```

---

## Docker Servisleri

| Container | Açıklama |
|---|---|
| rabam-mysql | MySQL veritabanı |
| rabam-rabbitmq | RabbitMQ mesaj kuyruğu |
| rabam-backend | Spring Boot backend servisi |
| rabam-frontend | Nginx üzerinde çalışan React frontend |

---

## Geliştirme Notları

Bu proje aşağıdaki başlıkları göstermek amacıyla geliştirilmiştir:

```text
Temiz katmanlı mimari
DTO kullanımı
Mapper katmanı
Validation
Global exception handling
İş kurallarının service katmanında uygulanması
Concurrency kontrolü
Optimistic locking
Pessimistic locking
RabbitMQ entegrasyonu
Docker ile full stack çalışma ortamı
Frontend i18n
Backend i18n
Modern React arayüzü
Otomatik testler
```


## CI Pipeline

Projede GitHub Actions CI pipeline bulunmaktadır. Her push ve pull request işleminde backend testleri ve frontend build işlemi otomatik olarak çalıştırılır.

```bash
mvn test
cd frontend && npm run build

---

## Geliştirici

Bu proje Münevver Verim tarafından geliştirilmiştir.
