# JobHunter Bot MVP - Setup and Usage Guide

## 🚀 Quick Start

### 1. Build the Application
```bash
./gradlew build
```

### 2. Run the Spring Boot API Server
```bash
./gradlew bootRun
```
The API server will start on `http://localhost:8080`

### 3. Access H2 Console (Development)
Navigate to: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./db/jobhunter`
- Username: `sa`
- Password: (empty)

## 📋 API Endpoints

### Resume Upload and Parsing
```bash
# Upload a PDF/DOCX resume for parsing
curl -X POST http://localhost:8080/api/resume/upload \
  -F "file=@sample-data/sample_resume.txt" \
  -H "Content-Type: multipart/form-data"
```

**Response:**
```json
{
  "success": true,
  "message": "Resume parsed successfully",
  "data": {
    "profileId": "123e4567-e89b-12d3-a456-426614174000",
    "name": "John Doe",
    "email": "john.doe@example.com",
    "location": "San Francisco, CA",
    "totalExperienceYears": 5,
    "titles": ["Senior Software Engineer", "Software Engineer"],
    "skills": ["Java", "Spring Framework", "React", "Docker", "AWS"],
    "rawText": "..."
  }
}
```

### Job Ingestion
```bash
# Ingest a single job
curl -X POST http://localhost:8080/api/jobs/ingest \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Senior Java Developer",
    "company": "TechCorp",
    "location": "San Francisco, CA",
    "description": "Java backend development role",
    "requiredSkills": ["Java", "Spring Framework", "MySQL"],
    "preferredSkills": ["Docker", "AWS"],
    "minExperience": 4,
    "maxExperience": 8,
    "salary": "$120,000 - $150,000",
    "jobUrl": "https://example.com/job1"
  }'

# Bulk ingest sample jobs
curl -X POST http://localhost:8080/api/jobs/bulk-ingest \
  -H "Content-Type: application/json" \
  -d @sample-data/sample_jobs.json
```

### Job Matching
```bash
# Get top 5 job matches for a candidate profile
curl -X GET "http://localhost:8080/api/match/{profileId}?limit=5"
```

**Response:**
```json
{
  "success": true,
  "message": "Matches found successfully",
  "data": [
    {
      "jobId": "456e7890-e89b-12d3-a456-426614174000",
      "title": "Senior Java Developer",
      "company": "TechCorp",
      "location": "San Francisco, CA",
      "matchedSkills": ["Java", "Spring Framework", "MySQL"],
      "missingRequiredSkills": [],
      "score": 0.92,
      "reason": "Excellent skill match (90%). Experience level fits well. Role title aligns with background. Location is a good match. Matched skills: Java, Spring Framework, MySQL.",
      "jobUrl": "https://example.com/job1"
    }
  ]
}
```

## 🧪 Running Tests

```bash
# Run all tests (excludes Selenium tests by default)
./gradlew test

# Run tests with auto-apply enabled (includes Selenium tests)
./gradlew test -Djobhunter.auto_apply=true
```

## ⚠️ Legacy Auto-Apply Mode

The legacy Selenium-based auto-apply functionality is **DISABLED by default** for safety.

To enable (admin only):
```bash
# Set environment variable
export AUTO_APPLY=true
./gradlew runLegacy

# Or set system property
./gradlew runLegacy -Djobhunter.auto_apply=true
```

## 🛠 Configuration

### Environment Variables
- `AUTO_APPLY`: Enable legacy auto-apply mode (default: false)
- `JOB_HUNTER_NAME`: Full name for applications
- `JOB_HUNTER_EMAIL`: Email address
- `JOB_HUNTER_PHONE`: Phone number
- `RESUME_PATH`: Path/URL to resume file
- `DISCORD_WEBHOOK_URL`: Discord notification webhook

### Application Properties
Key configuration options in `src/main/resources/application.properties`:

```properties
# Feature flags
jobhunter.auto_apply=false

# Database
spring.datasource.url=jdbc:h2:file:./db/jobhunter

# Matching algorithm weights (must sum to 1.0)
jobhunter.match.skill_weight=0.4
jobhunter.match.experience_weight=0.25
jobhunter.match.title_weight=0.2
jobhunter.match.location_weight=0.1
jobhunter.match.recency_weight=0.05
```

## 📊 Skills Normalization

The system automatically normalizes skills using a built-in database of canonical skill names and aliases:

- `java` → `Java`
- `spring boot` → `Spring Framework`
- `reactjs` → `React`
- `amazon web services` → `AWS`

Add new skills via the database or by modifying `DataLoader.java`.

## 🔧 Development

### Project Structure
```
src/main/java/com/jobhunter/
├── controller/          # REST API controllers
├── service/            # Business logic services
├── repository/         # JPA repositories
├── entity/            # Database entities
├── dto/               # Data transfer objects
├── config/            # Configuration classes
└── JobHunterApplication.java  # Spring Boot main class
```

### Key Services
- **ResumeParserService**: Extracts profile data from PDF/DOCX using Apache Tika
- **SkillNormalizerService**: Maps skill aliases to canonical names
- **MatchService**: Calculates job-candidate compatibility scores

### Matching Algorithm
The scoring algorithm considers:
1. **Skills (40%)**: Required/preferred skill overlap
2. **Experience (25%)**: Years of experience fit
3. **Title (20%)**: Job title similarity
4. **Location (10%)**: Geographic match
5. **Recency (5%)**: How recently the job was posted

## 🔒 Security Notes

1. **Auto-apply is disabled by default** - prevents accidental automated job applications
2. **Feature flag protection** - Selenium code only runs when explicitly enabled
3. **CI safety** - GitHub Actions excludes Selenium tests unless manually triggered
4. **Input validation** - File type restrictions and size limits on uploads

## 📈 Production Deployment

For production use:
1. Configure a production database (PostgreSQL recommended)
2. Set appropriate file upload limits
3. Enable HTTPS and secure endpoints
4. Consider rate limiting for public APIs
5. Monitor and log all matching activities

## 🤝 Contributing

1. All new Selenium/automation code must be gated behind feature flags
2. Add unit tests for new services and endpoints
3. Update documentation for API changes
4. Follow existing code style and patterns