# Push Instructions

The ElectricianApp repository has been set up locally with the following structure:

```
ElectricianApp/
├── app/                 # Main application module 
├── data/                # Data layer (Room DB, Repositories)
├── domain/              # Domain layer (Business logic, Models, UseCases)
└── feature/             # Feature modules
    ├── box/             # Box fill calculation feature
    ├── conduit/         # Conduit fill calculation feature
    └── dwelling/        # Dwelling load calculation feature
```

Everything has been committed locally. To push the code to the GitHub repository, follow these steps:

## Option 1: Using GitHub Personal Access Token (Recommended)

1. Create a Personal Access Token (PAT) from GitHub:
   - Go to GitHub.com → Your profile → Settings → Developer settings → Personal access tokens → Tokens (classic)
   - Generate a new token with 'repo' permissions
   - Copy the token when it's displayed (it won't be shown again)

2. Push to GitHub with your token:
   ```
   cd "C:\Users\Chris Boyd\Documents\GitHub\ElectricianApp"
   git push -u origin main
   ```
   
3. When prompted for credentials:
   - Username: Your GitHub username
   - Password: Use the personal access token (not your GitHub password)

## Option 2: Using GitHub CLI

If you have GitHub CLI installed:

1. Authenticate with GitHub CLI:
   ```
   gh auth login
   ```
   Follow the interactive prompts

2. Push to GitHub:
   ```
   cd "C:\Users\Chris Boyd\Documents\GitHub\ElectricianApp"
   git push -u origin main
   ```

## Option 3: Set Up SSH Authentication

For a more permanent solution:

1. Generate an SSH key (if you don't already have one):
   ```
   ssh-keygen -t ed25519 -C "your_email@example.com"
   ```

2. Add the SSH key to the ssh-agent:
   ```
   eval "$(ssh-agent -s)"
   ssh-add ~/.ssh/id_ed25519
   ```

3. Add the SSH key to your GitHub account:
   - Copy the public key: `cat ~/.ssh/id_ed25519.pub`
   - Go to GitHub.com → Your profile → Settings → SSH and GPG keys → New SSH key
   - Paste the public key and save

4. Change the repository URL to use SSH:
   ```
   git remote set-url origin git@github.com:Zevas1993/ElectricianApp.git
   ```

5. Push to GitHub:
   ```
   git push -u origin main
   ```

## Note on Credentials

Your local Git repository has already been configured with:

```
git remote add origin https://github.com/Zevas1993/ElectricianApp.git
```

You just need to authenticate to complete the push.
