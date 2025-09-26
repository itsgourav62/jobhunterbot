# 🤖 Job Hunter Bot - Automated Job Search & Application System

[![CI/CD](https://github.com/yourusername/jobhunterbot/actions/workflows/job-hunter-ci.yml/badge.svg)](https://github.com/yourusername/jobhunterbot/actions)
[![Java](https://img.shields.io/badge/Java-24-orange.svg)](https://openjdk.java.net/)
[![Selenium](https://img.shields.io/badge/Selenium-4.15-green.svg)](https://selenium.dev/)

An intelligent, automated job hunting system that searches for jobs across multiple platforms, matches them against your resume, and applies automatically. Built with Java, Selenium, and modern CI/CD practices.

## 🚀 Features

### 🔍 **Multi-Platform Job Fetching**
- **Naukri.com** - India's largest job portal
- **Indeed** - Global job search engine  
- **AngelList/Wellfound** - Startup ecosystem jobs
- **Glassdoor** - Company reviews + jobs
- **RemoteOK API** - Remote work opportunities (TOS-compliant)

### 🎯 **Intelligent Job Matching**
- Advanced skill matching with synonyms
- Experience level analysis
- Keyword sentiment analysis (positive/negative terms)
- Weighted scoring algorithm (60% skills, 25% experience, 15% keywords)
- Real-time matching analytics

### 🤖 **Automated Application Process**
- Browser automation with Selenium WebDriver
- Auto-fill personal details (name, email, phone)  
- Resume upload automation
- Rate-limiting and anti-detection measures
- Application tracking and duplicate prevention

### 📊 **Smart Analytics & Reporting**
- Match score analytics (0-100% compatibility)
- Daily application limits and tracking
- Performance metrics and success rates
- Discord/Email notifications with detailed reports

### 🔄 **CI/CD Automation**
- **GitHub Actions** for automated daily runs
- Headless browser support for cloud execution
- API-only mode for faster, TOS-compliant searches
- Configurable scheduling (daily at 9 AM UTC / 2:30 PM IST)
- Environment-based configuration management

## 📋 Quick Start

### Prerequisites
- **Java 24** (Oracle JDK recommended)
- **Chrome/Chromium Browser** (for Selenium)
- **Git** for version control

### 🛠️ Local Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/jobhunterbot.git
   cd jobhunterbot
   ```

2. **Configure environment variables**
   ```bash
   cp .env.example .env
   # Edit .env with your details:
   ```
   
   ```bash
   JOB_HUNTER_EMAIL=your.email@gmail.com
   JOB_HUNTER_PHONE=+91XXXXXXXXXX
   RESUME_PATH=/path/to/your/resume.pdf
   DISCORD_WEBHOOK_URL=https://discord.com/api/webhooks/...
   EMAIL_FROM=your.email@gmail.com
   EMAIL_PASSWORD=your_app_password
   ```

3. **Test setup**
   ```bash
   ./gradlew testSetup
   ```

4. **Run the bot**
   ```bash
   # Full job search with browser automation
   ./gradlew runBot
   
   # API-only search (faster, TOS-compliant)
   ./gradlew runAPIJob
   ```

## 🔧 Configuration

### Environment Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `JOB_HUNTER_EMAIL` | Your email for applications | `john@example.com` |
| `JOB_HUNTER_PHONE` | Phone number | `+911234567890` |
| `RESUME_PATH` | Path to resume file | `/path/to/resume.pdf` |
| `DISCORD_WEBHOOK_URL` | Discord notifications | `https://discord.com/api/webhooks/...` |
| `EMAIL_FROM` | Email sender | `alerts@gmail.com` |
| `EMAIL_PASSWORD` | Email app password | `your_app_password` |

### Skills Configuration
Edit your skills in `Main.java` or `APIJobRunner.java`:
```java
Arrays.asList("Java", "Spring", "React", "Python", "Node.js", "SQL", "JavaScript")
```

## 🔄 GitHub Actions Automation

### Setup GitHub Secrets
1. Go to your repository → Settings → Secrets and Variables → Actions
2. Add these secrets:
   - `JOB_HUNTER_EMAIL`
   - `JOB_HUNTER_PHONE` 
   - `RESUME_PATH`
   - `DISCORD_WEBHOOK_URL`
   - `EMAIL_FROM`
   - `EMAIL_PASSWORD`

### Automated Workflows

#### 🕘 **Daily Job Search** (Scheduled)
- Runs every day at 9 AM UTC (2:30 PM IST)
- Full job search across all platforms
- Automatic application to top matches
- Discord notifications with results

#### 🌐 **API-Only Search** (Manual)
- Faster execution using only APIs
- TOS-compliant job fetching
- Perfect for testing and frequent runs
- Trigger: `Actions` → `Run workflow` → Select "api_only"

#### ✅ **Health Check** (On Push)  
- Validates code changes
- Tests Gradle build
- Ensures system health

### Manual Triggers
```bash
# Via GitHub Actions UI:
Actions → Job Hunter Bot CI/CD → Run workflow
```

Choose run type:
- `test` - Health check only
- `full_job_search` - Complete job search with browser automation
- `api_only` - API-based search only

## 📊 Understanding Match Scores

The bot uses a weighted scoring algorithm:

- **Skills Match (60%)**: Direct skill mentions + synonyms
- **Experience Level (25%)**: Senior vs junior role analysis  
- **Keywords Analysis (15%)**: Positive/negative terms

### Score Ranges:
- **90-100%**: Perfect match - high priority
- **70-89%**: Excellent match - apply immediately  
- **60-69%**: Good match - worth applying
- **40-59%**: Fair match - consider manually
- **Below 40%**: Poor match - skip

## 🚀 Architecture

### Core Components:

```
📦 com.jobhunter
 ┣ 📂 autofill          # Browser automation & form filling
 ┣ 📂 config           # Application configuration  
 ┣ 📂 cron             # Job scheduling & orchestration
 ┣ 📂 fetcher          # Job platform scrapers/APIs
 ┣ 📂 matcher          # Resume-job matching engine
 ┣ 📂 model            # Data models (Job, Resume)
 ┣ 📂 notifier         # Discord/Email notifications
 ┣ 📂 parser           # Resume parsing (PDF/DOCX)
 ┗ 📂 storage          # Database & application tracking
```

### Job Fetcher Architecture:

- **Scraping Fetchers**: Naukri, Indeed, AngelList, Glassdoor
- **API Fetchers**: RemoteOK (TOS-compliant, faster)
- **Parallel Processing**: Concurrent job fetching for better performance
- **Rate Limiting**: Built-in delays to avoid blocking

## 🛡️ Compliance & Best Practices

### ✅ **TOS-Compliant Options**
- **RemoteOK API**: Official API, no scraping
- **API-only mode**: Use `./gradlew runAPIJob` for compliant runs
- **Rate limiting**: Automatic delays between requests

### ⚠️ **Scraping Considerations**  
- **LinkedIn**: Removed due to TOS violations
- **Respectful scraping**: Built-in delays and limits
- **User-Agent rotation**: Reduces detection risk

### 🔒 **Security**
- Environment variable configuration
- No hardcoded credentials
- GitHub Secrets for CI/CD
- Resume path flexibility (local or cloud)

## 🐛 Troubleshooting

### Common Issues:

1. **Browser not found**
   ```bash
   # Install Chrome/Chromium
   # For Ubuntu:
   sudo apt-get install chromium-browser
   ```

2. **Java version mismatch**
   ```bash
   # Check Java version
   java -version
   # Should be Java 24+
   ```

3. **Selenium WebDriver issues**
   ```bash
   # Clear browser cache, restart
   # Check Chrome version compatibility
   ```

4. **No jobs found**
   - Check your skills configuration
   - Try API-only mode: `./gradlew runAPIJob`
   - Verify internet connection

5. **CI/CD failures**
   - Check GitHub Secrets are properly set
   - Verify Discord webhook URL format
   - Check logs in Actions tab

## 📈 Performance Optimization

### Speed Improvements:
- **Parallel fetching**: Multiple sources simultaneously
- **API-first approach**: Prioritize API over scraping  
- **Headless browser**: Faster execution in CI/CD
- **Smart caching**: Avoid duplicate job processing

### Resource Management:
- **Connection pooling**: Efficient HTTP requests
- **Memory optimization**: Cleanup after each run
- **Timeout handling**: Prevent hanging processes

## 🤝 Contributing

1. Fork the repository
2. Create feature branch: `git checkout -b feature/amazing-feature`
3. Add your job fetcher in `com.jobhunter.fetcher`
4. Update `JobFetcherFactory` to register new fetcher
5. Test thoroughly: `./gradlew test`
6. Submit pull request

### Adding New Job Platforms:
```java
// Implement JobFetcher interface
public class NewPlatformFetcher implements JobFetcher {
    @Override
    public List<Job> fetchJobs(List<String> skills) {
        // Your implementation
    }
}

// Register in JobFetcherFactory
this.scrapingFetchers.add(new NewPlatformFetcher());
```

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## ⚖️ Disclaimer

This tool is for educational and personal use. Users are responsible for complying with job platform Terms of Service. The authors are not responsible for any misuse or violations.

---

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