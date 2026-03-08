$ErrorActionPreference = "Stop"

$repoPath = "C:\Users\lohit\Desktop\AIFinanceAdvisor"
$logPath = "C:\Users\lohit\Desktop\AIFinanceAdvisor\scripts\auto-commit.log"

function Write-Log {
    param([string]$Message)
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    "$timestamp $Message" | Out-File -FilePath $logPath -Append -Encoding utf8
}

try {
    Set-Location $repoPath

    $changes = git status --porcelain
    if (-not $changes) {
        Write-Log "No changes detected. Skipping commit."
        exit 0
    }

    git add -A

    $commitTime = Get-Date -Format "yyyy-MM-dd HH:mm"
    git commit -m "chore(auto): scheduled commit $commitTime"

    if ($LASTEXITCODE -ne 0) {
        Write-Log "Commit failed or nothing to commit after add."
        exit 1
    }

    $branch = (git rev-parse --abbrev-ref HEAD).Trim()
    if (-not $branch) {
        Write-Log "Could not detect current branch."
        exit 1
    }

    git push origin $branch
    if ($LASTEXITCODE -eq 0) {
        Write-Log "Commit and push successful on branch '$branch'."
        exit 0
    }

    Write-Log "Push failed on branch '$branch'."
    exit 1
}
catch {
    Write-Log "Unhandled error: $($_.Exception.Message)"
    exit 1
}
