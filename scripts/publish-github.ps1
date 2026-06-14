param(
    [string]$RepoName = "FinalHwm",
    [string]$Owner = $env:GITHUB_OWNER,
    [ValidateSet("private", "public")]
    [string]$Visibility = "private",
    [string]$Branch = "main",
    [string]$RemoteName = "origin",
    [string]$CommitMessage = "Update project",
    [switch]$SkipCommit,
    [switch]$NoCreate
)

$ErrorActionPreference = "Stop"

function Invoke-Step {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Command,
        [string[]]$Arguments = @()
    )

    & $Command @Arguments
    if ($LASTEXITCODE -ne 0) {
        throw "Command failed: $Command $($Arguments -join ' ')"
    }
}

function Test-CommandExists {
    param([Parameter(Mandatory = $true)][string]$Name)
    return [bool](Get-Command $Name -ErrorAction SilentlyContinue)
}

function Get-GitHubHeaders {
    if (-not $env:GITHUB_TOKEN) {
        return $null
    }

    return @{
        Authorization          = "Bearer $env:GITHUB_TOKEN"
        Accept                 = "application/vnd.github+json"
        "X-GitHub-Api-Version" = "2022-11-28"
        "User-Agent"           = "FinalHwm-publish-script"
    }
}

function Get-GitHubLoginFromToken {
    param([Parameter(Mandatory = $true)][hashtable]$Headers)
    $user = Invoke-RestMethod -Method Get -Uri "https://api.github.com/user" -Headers $Headers
    return $user.login
}

function Test-GitHubRepoExists {
    param(
        [Parameter(Mandatory = $true)][string]$FullName,
        [hashtable]$Headers
    )

    if (Test-CommandExists "gh") {
        & gh repo view $FullName *> $null
        return ($LASTEXITCODE -eq 0)
    }

    if ($Headers) {
        try {
            Invoke-RestMethod -Method Get -Uri "https://api.github.com/repos/$FullName" -Headers $Headers *> $null
            return $true
        }
        catch {
            if ($_.Exception.Response -and $_.Exception.Response.StatusCode.value__ -eq 404) {
                return $false
            }
            throw
        }
    }

    return $false
}

function New-GitHubRepo {
    param(
        [Parameter(Mandatory = $true)][string]$Owner,
        [Parameter(Mandatory = $true)][string]$RepoName,
        [Parameter(Mandatory = $true)][string]$Visibility,
        [hashtable]$Headers
    )

    $fullName = "$Owner/$RepoName"
    if (Test-CommandExists "gh") {
        Invoke-Step "gh" @("repo", "create", $fullName, "--$Visibility", "--confirm")
        return
    }

    if (-not $Headers) {
        throw "Cannot create GitHub repo automatically. Install GitHub CLI (`gh auth login`) or set GITHUB_TOKEN and GITHUB_OWNER."
    }

    $login = Get-GitHubLoginFromToken -Headers $Headers
    $body = @{
        name       = $RepoName
        private    = ($Visibility -eq "private")
        auto_init  = $false
    } | ConvertTo-Json

    if ($Owner -eq $login) {
        Invoke-RestMethod -Method Post -Uri "https://api.github.com/user/repos" -Headers $Headers -Body $body -ContentType "application/json" *> $null
    }
    else {
        Invoke-RestMethod -Method Post -Uri "https://api.github.com/orgs/$Owner/repos" -Headers $Headers -Body $body -ContentType "application/json" *> $null
    }
}

if (-not (Test-CommandExists "git")) {
    throw "git is not available on PATH."
}

if (-not (Test-Path ".git")) {
    Invoke-Step "git" @("init")
}

if (-not $Owner -and (Test-CommandExists "gh")) {
    $Owner = (& gh api user --jq ".login").Trim()
    if ($LASTEXITCODE -ne 0) {
        $Owner = $null
    }
}

if (-not $Owner) {
    throw "GitHub owner is unknown. Pass -Owner <github-user> or set GITHUB_OWNER."
}

if (-not $SkipCommit) {
    Invoke-Step "git" @("add", "-A")
    & git diff --cached --quiet
    if ($LASTEXITCODE -ne 0) {
        Invoke-Step "git" @("commit", "-m", $CommitMessage)
    }
}

Invoke-Step "git" @("branch", "-M", $Branch)

$remoteUrl = "https://github.com/$Owner/$RepoName.git"
& git remote get-url $RemoteName *> $null
if ($LASTEXITCODE -eq 0) {
    Invoke-Step "git" @("remote", "set-url", $RemoteName, $remoteUrl)
}
else {
    Invoke-Step "git" @("remote", "add", $RemoteName, $remoteUrl)
}

$headers = Get-GitHubHeaders
$fullName = "$Owner/$RepoName"
if (-not $NoCreate -and -not (Test-GitHubRepoExists -FullName $fullName -Headers $headers)) {
    New-GitHubRepo -Owner $Owner -RepoName $RepoName -Visibility $Visibility -Headers $headers
}

Invoke-Step "git" @("push", "-u", $RemoteName, $Branch)
