# 🚀 GitHub Actions Setup Guide for Job Hunter Bot

## 📋 Quick Setup Checklist

### 1. 🔐 Set up GitHub Secrets

Go to your repository → **Settings** → **Secrets and Variables** → **Actions** → **New repository secret**

Add these secrets:

| Secret Name | Value | Example |
|-------------|-------|---------|
| `JOB_HUNTER_NAME` | Your full name | `Gourav Shaw` |
| `JOB_HUNTER_EMAIL` | Your email | `shawgourav62@gmail.com` |
| `JOB_HUNTER_PHONE` | Your phone | `+91-0748719503` |
| `RESUME_PATH` | Your resume URL | `https://drive.google.com/uc?id=...` |
| `DISCORD_WEBHOOK_URL` | Discord webhook | `https://discord.com/api/webhooks/...` |

### 2. 🎯 Your Resume URL (Google Drive)

**Current Resume URL:** 
```
https://drive.google.com/uc?id=1zEj4gThEUm1eqdyqwdM8ihIcct3Rcosi&export=download
```

**Make sure your resume is:**
- ✅ Publicly accessible (anyone with link can view)
- ✅ Using the direct download format above

### 3. 📱 Your Discord Webhook

**Current Webhook:** 
```
https://discord.com/api/webhooks/1421240031720509622/sXpcA3ZztxBx3SpZcgx8ZjWQMsCevSUipdosEGFsxslScfMTE9vBB0mPT4s4D1nYbDgo
```

## 🤖 How the Automation Works

### 📅 **Daily Schedule**
- **Runs automatically** every day at **2:30 PM IST** (9:00 AM UTC)
- **Perfect timing** - during lunch break when you can check Discord!

### 🔄 **What Happens Each Day**
1. **🔍 Job Search** - Scans all job sources for opportunities
2. **🧠 Analysis** - Matches jobs against your skills and experience
3. **📊 Scoring** - Rates each job (0-100% compatibility)
4. **📄 File Generation** - Creates your personalized application dashboard
5. **📱 Discord Alert** - Sends you top opportunities via Discord
6. **💾 Storage** - Saves files to GitHub Actions artifacts

### 📱 **Discord Notifications Include:**
- 🔥 **Priority jobs** (80%+ match)
- ⭐ **High potential jobs** (70%+ match)
- 📊 **Summary statistics**
- 🔗 **Links to application files**

## 🎮 **Manual Controls**

### 🚀 **Run Job Search Anytime**
1. Go to **Actions** tab in your repo
2. Click **Job Hunter Bot - Daily Smart Application Workflow**
3. Click **Run workflow**
4. Choose run type:
   - `smart_apply` - Full smart workflow (recommended)
   - `api_only` - Quick API search only
   - `test_setup` - Health check

### 📄 **Download Your Files**
1. Go to **Actions** tab
2. Click on any completed run
3. Download **job-applications-[number]** artifact
4. Extract to get your HTML dashboard and markdown files

## 📊 **What You Get Each Day**

### 📱 **Discord Notification**
```
🎯 SMART JOB ALERTS - 2025-09-27

🔥 PRIORITY APPLICATIONS (3 jobs)
• Senior Engineer at Greenlight (77%)
• Python Developer at TechCorp (74%)
• Full Stack Developer at StartupXYZ (73%)

⭐ HIGH POTENTIAL (8 jobs)

📊 SUMMARY:
• Total analyzed: 35 jobs
• Avg match score: 64.2%
• Files generated: quick-apply-2025-09-27.html
```

### 🌐 **HTML Dashboard** (`quick-apply-[timestamp].html`)
- Beautiful, mobile-friendly interface
- One-click "🚀 APPLY NOW" buttons
- Color-coded by match quality
- Direct links to job applications

### 📋 **Markdown Report** (`job-applications-[timestamp].md`)
- Structured list for easy reference
- Organized by priority tiers
- Match scores and company info

## 🔧 **Troubleshooting**

### ❌ **If GitHub Actions Fail:**
1. Check **Actions** tab for error details
2. Verify all secrets are set correctly
3. Ensure Discord webhook is valid
4. Check if resume URL is accessible

### 📱 **If Discord Notifications Stop:**
1. Verify webhook URL hasn't changed
2. Check Discord channel permissions
3. Test webhook manually: `curl -X POST [webhook_url] -H "Content-Type: application/json" -d '{"content": "test"}'`

### 🔗 **If Resume Download Fails:**
1. Verify Google Drive link is public
2. Use the direct download format: `https://drive.google.com/uc?id=FILE_ID&export=download`
3. Test the link in incognito browser

## 🎯 **Expected Daily Workflow**

### **2:30 PM IST (Every Day):**
1. 📱 **Check Discord** - You'll get notification with top jobs
2. 🌐 **Download files** from GitHub Actions artifacts  
3. 📋 **Open HTML dashboard** in browser
4. 🚀 **Click "APPLY NOW"** for priority jobs (80%+ match)
5. 📝 **Apply manually** on company websites
6. ✅ **Track applications** yourself

### **Weekly Review:**
- 📊 **Check match score trends** 
- 🎯 **Update skills** if needed
- 📈 **Optimize resume** based on insights

## 🎉 **You're All Set!**

Your Job Hunter Bot will now work 24/7, finding the best opportunities and delivering them directly to your Discord. You focus on applying to the highest-quality matches!

**Next Steps:**
1. ✅ Set up all GitHub secrets
2. ✅ Push code to main branch  
3. ✅ Wait for 2:30 PM IST or trigger manually
4. ✅ Check Discord for your first job alerts!

*Happy Job Hunting! 🚀*