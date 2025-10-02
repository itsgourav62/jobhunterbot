# 🔒 GitHub Secrets Setup Guide

To enable job notifications and personalized job matching, you need to add the following secrets to your GitHub repository.

## 📍 Where to Add Secrets

1. Go to: https://github.com/itsgourav62/jobhunterbot/settings/secrets/actions
2. Click "New repository secret" for each secret below

## 🔐 Required Secrets

### Discord Notifications
```
Name: DISCORD_WEBHOOK_URL
Value: [Your Discord webhook URL - keep this private!]
```

### Personal Information
```
Name: JOB_HUNTER_NAME
Value: [Your full name]

Name: JOB_HUNTER_EMAIL
Value: [Your email address]

Name: EMAIL_TO
Value: [Your email address for notifications]

Name: RESUME_PATH
Value: [Your resume URL from Google Drive or other public link]
```

### Email Configuration (Optional)
If you want email notifications in addition to Discord:
```
Name: EMAIL_FROM
Value: [Your sender email address]

Name: EMAIL_PASSWORD
Value: [Your email app password]
```

## 🛡️ Security Notes

- **Never commit secrets to your repository**
- **Never share your webhook URLs publicly**
- **Use GitHub secrets for all sensitive information**
- **Repository secrets are encrypted and only visible to you**

## ✅ After Setup

Once you've added the secrets:
1. The job search will run automatically every 12 hours (6 AM & 6 PM UTC)
2. You'll get Discord notifications with job matches
3. Job reports will be available as GitHub Actions artifacts
4. Manual runs can be triggered from GitHub Actions tab

## 🚀 Test Your Setup

To test immediately:
1. Go to Actions → "Job Hunter Bot" → "Run workflow"
2. Select "smart_apply" from the dropdown
3. Click "Run workflow"
4. Check Discord for notifications and GitHub artifacts for job reports