# 🤝 Contributing to Job Hunter Bot

Thank you for considering contributing to Job Hunter Bot! This guide will help you get started.

## 🚀 Quick Start for Contributors

1. **Fork & Clone**
   ```bash
   git clone https://github.com/yourusername/jobhunterbot.git
   cd jobhunterbot
   ```

2. **Set up development environment**
   ```bash
   cp .env.example .env
   # Edit .env with your test values
   ./gradlew testSetup
   ```

3. **Create feature branch**
   ```bash
   git checkout -b feature/your-awesome-feature
   ```

## 🔧 Development Guidelines

### Code Style
- Follow Java naming conventions
- Use meaningful variable and method names
- Add JavaDoc comments for public methods
- Keep methods small and focused
- Use proper exception handling

### Architecture
```
📦 com.jobhunter
 ┣ 📂 fetcher      # Add new job platform scrapers here
 ┣ 📂 matcher      # Improve job matching algorithms
 ┣ 📂 autofill     # Enhance form filling automation
 ┣ 📂 notifier     # Add new notification channels
 ┗ 📂 config       # Configuration management
```

## 🌟 How to Add New Job Platforms

### 1. Create Fetcher Class
```java
package com.jobhunter.fetcher;

public class YourPlatformFetcher implements JobFetcher {
    @Override
    public List<Job> fetchJobs(List<String> skills) {
        // Your implementation
        // Return List<Job> with id, title, description, url, company
    }
}
```

### 2. Register in Factory
```java
// In JobFetcherFactory.java
this.scrapingFetchers.add(new YourPlatformFetcher()); // For scraping
// OR
this.apiFetchers.add(new YourPlatformFetcher()); // For APIs
```

### 3. Test Your Fetcher
```bash
./gradlew build
./gradlew runAPIJob  # For API-based fetchers
./gradlew runBot     # For scraping fetchers
```

## 📊 Common Contribution Areas

### 🎯 High-Priority (Most Needed)
1. **New Job Platform Integrations**
   - Freshersworld.com
   - TimesJobs.com
   - Monster.com
   - Shine.com
   - Instahyre.com

2. **API-Based Sources** (Preferred for TOS compliance)
   - GitHub Jobs API (if available)
   - Stack Overflow Jobs API
   - AngelList API integration
   - Any official job APIs

3. **Enhanced Matching Algorithms**
   - Machine learning-based matching
   - Salary range analysis
   - Location preference matching
   - Company culture fit analysis

### 🔧 Medium-Priority
1. **Better Browser Automation**
   - Captcha handling
   - Dynamic content loading
   - Multi-browser support
   - Mobile-first platforms

2. **Advanced Notifications**
   - Slack integration
   - Telegram bot
   - WhatsApp notifications
   - Email templates

3. **Database Enhancements**
   - PostgreSQL support
   - Application tracking
   - Success rate analytics
   - Duplicate detection

### 🎨 Nice-to-Have
1. **Web Dashboard**
   - Real-time job monitoring
   - Application status tracking
   - Analytics and reports
   - Configuration management

2. **Mobile App**
   - React Native or Flutter
   - Push notifications
   - Quick application approval

## 🧪 Testing

### Test Your Changes
```bash
# Run all tests
./gradlew test

# Test specific functionality
./gradlew runAPIJob   # Test API fetchers
./gradlew runBot      # Test full application

# Build and verify
./gradlew clean build
```

### Test Data
- Use MockJobFetcher for testing
- Don't commit real credentials
- Test with various skill combinations

## 📝 Pull Request Guidelines

### Before Submitting
- [ ] Code compiles without errors
- [ ] Tests pass locally
- [ ] No sensitive data in commits
- [ ] Feature works with both API and scraping modes
- [ ] README updated if needed

### PR Description Template
```markdown
## What Changed
Brief description of your changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Performance improvement
- [ ] Documentation update

## Testing
- [ ] Tested locally
- [ ] Added new tests
- [ ] Verified CI/CD compatibility

## Screenshots (if applicable)
Add screenshots of new features
```

## 🔒 Security Guidelines

### ❌ Never Commit
- Real email credentials
- Personal resume files
- Discord webhook URLs
- Database passwords
- API keys or tokens

### ✅ Always Use
- Environment variables for secrets
- Generic test data
- Placeholder configurations
- .env.example for documentation

## 🤖 CI/CD Integration

Your changes should work with GitHub Actions:

```yaml
# Test your changes work in CI/CD
- API-only mode for faster testing
- Headless browser support
- Environment variable configuration
- Proper error handling
```

## 📚 Resources

### Selenium WebDriver
- [Selenium Documentation](https://selenium.dev/documentation/)
- [WebDriver Best Practices](https://selenium.dev/documentation/test_practices/)

### Java Development
- [Java 24 Features](https://openjdk.java.net/projects/jdk/24/)
- [Gradle User Guide](https://docs.gradle.org/current/userguide/userguide.html)

### Job Platform APIs
- [RemoteOK API](https://remoteok.com/remote-dev)
- [GitHub Jobs](https://jobs.github.com/api) (deprecated but reference)

## 🎯 Specific Help Needed

### 🚨 Critical Needs
1. **LinkedIn Alternative** - Need TOS-compliant job source
2. **Rate Limiting** - Better anti-detection for scraping
3. **Resume Parsing** - Improve PDF/DOCX extraction
4. **Application Success Tracking** - Detect successful applications

### 💡 Feature Ideas
- AI-powered resume customization per job
- Interview scheduling integration
- Salary negotiation suggestions
- Company research automation

## 🏆 Recognition

Contributors will be:
- Added to contributors list in README
- Mentioned in release notes
- Given credit in code comments
- Featured in project updates

## 💬 Getting Help

- **Issues**: Create GitHub issue for bugs/features
- **Discussions**: Use GitHub Discussions for questions
- **Discord**: Join our Discord server (if available)
- **Email**: Contact maintainers directly

## 📄 License

By contributing, you agree that your contributions will be licensed under the MIT License.

---

**Happy Contributing! 🚀**

Every contribution, no matter how small, makes Job Hunter Bot better for everyone!