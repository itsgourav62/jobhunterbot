# 🤖 Job Hunter Bot - Intelligent Job Matching System

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.14-green.svg)](https://spring.io/projects/spring-boot)
[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)](https://github.com/itsgourav62/jobhunterbot)

An intelligent job matching system that fetches jobs from multiple API providers, parses your resume to extract skills and experience, and intelligently matches job postings against your profile. Built with Java 17, Spring Boot, and reactive WebClient.

## 🚀 Features

### 🔍 **Multi-Source Job Fetching**
- **Remotive API** - Remote job opportunities
- **RemoteOK API** - Remote work listings
- **Adzuna API** - Comprehensive job search engine
- **Arbeitnow API** - Job postings aggregator
- **Jooble API** - Multi-country job search
- **Y Combinator / Hacker News API** - Startup and tech jobs

### 📄 **Resume Parsing & Skill Extraction**
- PDF and text resume support using Apache Tika
- Automatic name and experience extraction via regex patterns
- Intelligent skill detection from predefined skill keywords
- Target job title identification (Backend Engineer, Frontend Engineer, etc.)
- Support for both file uploads and text input

### 🎯 **Intelligent Job Matching Algorithm**
- Multi-factor matching: 70% skill match + 30% job title match
- Skill-based filtering against job descriptions
- Experience level consideration
- Real-time match score calculation (0-100% compatibility)
- Ranked results with matching reasons

### 🌐 **Web-Based User Interface**
- Bootstrap 5 responsive design
- Resume upload and paste functionality
- Real-time job matching results display
- Detailed job cards with match explanations
- Clean, intuitive UX

### ⚡ **Spring Boot Architecture**
- Reactive WebClient for non-blocking API calls
- Plugin-based job provider system via Spring Components
- Parallel job fetching from multiple providers
- Dependency injection and Spring annotations
- RESTful controller-based design

## 📋 Quick Start

### Prerequisites
- **Java 17** (Oracle JDK or OpenJDK)
- **Gradle** (included via gradlew)
- Modern web browser (for web interface)

### 🛠️ Local Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/itsgourav62/jobhunterbot.git
   cd jobhunterbot
   ```

2. **Configure API keys** (optional, some providers don't require keys)
   ```bash
   # Create .env or set environment variables for optional APIs
   export ADZUNA_APP_ID=your_app_id
   export ADZUNA_APP_KEY=your_app_key
   export JOOBLE_API_KEY=your_api_key
   ```

3. **Build the project**
   ```bash
   ./gradlew clean build
   ```

4. **Run the application**
   ```bash
   ./gradlew bootRun
   ```
   
   The web server starts on `http://localhost:8080`

5. **Access the web interface**
   - Navigate to `http://localhost:8080`
   - Upload a resume (PDF or text)
   - Click "Find Matching Jobs"
   - View results with match scores and reasons

## 🔧 Configuration

### Application Properties

The application is configured via `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8080

# Job Provider API Endpoints
job.provider.remotive.url=https://remotive.com/api
job.provider.arbeitnow.url=https://www.arbeitnow.com
job.provider.remoteok.url=https://remoteok.com
job.provider.adzuna.url=https://api.adzuna.com/v1/api/jobs/gb
job.provider.jooble.url=https://jooble.org
job.provider.yc.url=https://hacker-news.firebaseio.com/v0
```

### Optional API Keys (Environment Variables)

| Variable | Provider | Required | Obtain From |
|----------|----------|----------|-------------|
| `ADZUNA_APP_ID` | Adzuna | Optional | [adzuna.com](https://developer.adzuna.com) |
| `ADZUNA_APP_KEY` | Adzuna | Optional | [adzuna.com](https://developer.adzuna.com) |
| `JOOBLE_API_KEY` | Jooble | Optional | [jooble.org](https://jooble.org/api) |

**Note:** Most job providers work without API keys. Free-tier APIs are used by default.

## 🏗️ Project Architecture

### Core Structure:

```
📦 com.jobhunter
 ┣ 📂 config           # Configuration (EnvConfig)
 ┣ 📂 controller       # Web controllers (WebController)
 ┣ 📂 model            # Data models (CandidateProfile, JobPosting, MatchedJob)
 ┣ 📂 service          # Business logic layer
 ┃  ┣ JobService.java         # Orchestrates job fetching from providers
 ┃  ┣ MatchingService.java     # Implements matching algorithm
 ┃  ┣ ResumeService.java       # Resume parsing & skill extraction
 ┃  ┗ provider/                # Job provider implementations (Remotive, RemoteOK, Adzuna, etc.)
 ┗ 📂 resources
    ┣ application.properties    # API configuration
    ┗ templates/index.html      # Web interface
```

### How It Works:

1. **User Upload**: Resume (PDF/text) uploaded via web interface
2. **Resume Parsing**: `ResumeService` extracts name, experience, skills using Apache Tika + regex
3. **Job Fetching**: `JobService` queries all `JobProvider` implementations in parallel
4. **Matching**: `MatchingService` scores jobs using skill and title matching (70%/30% weights)
5. **Display**: Results ranked by match score and displayed in UI

## 🔌 Job Providers (Pluggable Architecture)

Each job provider implements the `JobProvider` interface:

| Provider | URL | API Key Required | Status |
|----------|-----|------------------|--------|
| RemotiveJobProvider | remotive.com/api | ❌ No | ✅ Active |
| RemoteOKJobProvider | remoteok.com/api | ❌ No | ✅ Active |
| AdzunaJobProvider | api.adzuna.com | ✅ Optional | ✅ Active |
| ArbeitnowJobProvider | arbeitnow.com | ❌ No | ✅ Active |
| JoobleJobProvider | jooble.org | ✅ Optional | ✅ Active |
| YCombinatorJobProvider | hacker-news.firebaseio.com | ❌ No | ✅ Active |

All providers are automatically registered as Spring `@Component` beans and injected into `JobService`.

## 🎯 Matching Algorithm

The system uses a two-factor scoring model:

```
Match Score = (Skill Match × 0.7) + (Title Match × 0.3)
```

### Skill Matching (70% weight):
- Extracts skills from resume using predefined keywords
- Searches for skill mentions in job description
- Score = (matched skills / total skills) × 100%

### Title Matching (30% weight):  
- Checks if job title contains target job titles
- Binary: 1.0 if match found, 0.0 otherwise
- Example: "Backend Engineer" matches "Senior Backend Engineer"

### Result Filtering:
- Only jobs with score > 10% are displayed
- Results sorted in descending order by match score

## 📋 Detected Skills

The resume parser searches for these keywords:

```
java, spring, spring boot, sql, postgresql, mysql, docker, aws, 
kubernetes, python, javascript, react, go, microservices, rest, 
api, git, maven, gradle, jenkins, kafka
```

Add more skills by editing `ResumeService.SKILL_KEYWORDS` set.

## 🌐 Web Interface

### URL: `http://localhost:8080`

**Features:**
- Textarea for pasting resume text
- File upload for PDF/TXT resumes
- Real-time matching
- Display of parsed profile (name, skills, experience)
- Job results with:
  - Job title and company
  - Location
  - Match score and reason
  - Apply button linking to job posting

## 🚀 Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests ResumeParserTest

# Run with coverage
./gradlew test jacocoTestReport
```

## 🛠️ Technologies Used

- **Java 17**: Modern JDK with records, text blocks, and pattern matching
- **Spring Boot 2.7.14**: Framework for rapid application development
- **WebFlux**: Reactive, non-blocking HTTP client for API calls
- **Apache Tika 2.8.0**: Resume parsing (PDF/TXT support)
- **Jackson**: JSON serialization/deserialization
- **Thymeleaf**: Server-side template engine
- **Bootstrap 5**: Responsive UI framework
- **Gradle**: Build automation

## 🐛 Troubleshooting

### Common Issues:

1. **Job providers return empty results**
   - Verify API endpoints in `application.properties`
   - Check network connectivity
   - Some APIs may have rate limiting; wait a few minutes

2. **Resume parsing issues**
   - Ensure PDF is not password-protected
   - Try copying text and pasting directly
   - Check that skills are in the predefined keyword list

3. **ClassNotFoundException or build errors**
   ```bash
   ./gradlew clean build
   ```

4. **Port 8080 already in use**
   ```bash
   # Change port in application.properties
   server.port=8081
   ```

5. **WebClient errors**
   - Check internet connection
   - Verify API endpoints are accessible
   - Check application logs for detailed error messages

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Make changes and test thoroughly
4. Commit with clear messages: `git commit -m 'Add new job provider'`
5. Push to your fork: `git push origin feature/your-feature`
6. Submit a pull request

### Guidelines:
- Follow existing code style and patterns
- Add unit tests for new features
- Update README if adding new providers or features
- Ensure all tests pass: `./gradlew test`

### Adding a New Job Provider:
```java
@Component
public class NewJobProvider implements JobProvider {
    private final WebClient webClient;
    
    public NewJobProvider(WebClient.Builder builder, @Value("${job.provider.new.url}") String url) {
        this.webClient = builder.baseUrl(url).build();
    }
    
    @Override
    public List<JobPosting> getJobs(String... keywords) {
        // Fetch and map to JobPosting
        return Collections.emptyList();
    }
}
```

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Apache Tika** - For PDF/document parsing
- **Spring Boot Team** - For the excellent framework
- **Job API Providers** - Remotive, RemoteOK, Adzuna, and others for free APIs
- **Bootstrap** - For responsive UI components

## 💡 Future Improvements

- [ ] Machine learning-based matching algorithm
- [ ] Additional resume formats (DOCX, RTF)
- [ ] Email notifications with job matches
- [ ] User authentication and saved profiles
- [ ] Dashboard with matching statistics
- [ ] Integration with LinkedIn/Indeed
- [ ] Scheduled job searches via Quartz scheduler
- [ ] Docker containerization for easy deployment

## 📞 Support

For issues, feature requests, or questions:
1. Check existing [GitHub Issues](https://github.com/itsgourav62/jobhunterbot/issues)
2. Create a new issue with detailed description
3. Include stack traces and logs if reporting bugs

---

**Built with ❤️ by Gourav | [GitHub](https://github.com/itsgourav62) | [Portfolio](#)**

## 🎯 Roadmap

- [ ] **Machine Learning**: Advanced resume-job matching
- [ ] **Web Dashboard**: Real-time monitoring interface  
- [ ] **Mobile App**: Job alerts and management
- [ ] **Integration**: More job platforms and APIs
- [ ] **Analytics**: Advanced reporting and insights
- [ ] **AI Resume**: Dynamic resume generation per job

---

**Happy Job Hunting! 🚀**

Made with ❤️ for developers by developers