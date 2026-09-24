---
# Trigger - when should this workflow run?
on:
  workflow_dispatch:  # Manual trigger

# Alternative triggers (uncomment to use):
# on:
#   issues:
#     types: [opened, reopened]
#   pull_request:
#     types: [opened, synchronize]
#   schedule: daily  # Fuzzy daily schedule (scattered execution time)
#   # schedule: weekly on monday  # Fuzzy weekly schedule

# Permissions - what can this workflow access?
# Write operations (creating issues, PRs, comments, etc.) are handled
# automatically by the safe-outputs job with its own scoped permissions.
permissions:
  contents: read
  issues: read
  pull-requests: read

# AI engine to use for this workflow
engine: copilot

# Tools - GitHub API access via toolsets (context, repos, issues, pull_requests)
# tools:
#   github:
#     toolsets: [default]

# Network access
network: defaults

# Outputs - what APIs and tools can the AI use?
safe-outputs:
  create-issue:          # Creates issues (default max: 1)
    max: 5               # Optional: specify maximum number
  # actions:
  # activation-comments:
  # add-comment:
  # add-labels:
  # add-reviewer:
  # ado-assign-work-item:
  # ado-comment-on-work-item:
  # ado-create-work-item:
  # ado-link-work-items:
  # ado-update-work-item:
  # ado-upload-workitem-attachment:
  # allowed-github-references:
  # approve-workflow-run:
  # assign-milestone:
  # assign-to-agent:
  # assign-to-user:
  # autofix-code-scanning-alert:
  # call-workflow:
  # close-discussion:
  # close-issue:
  # close-pull-request:
  # concurrency-group:
  # create-agent-session:
  # create-agent-task:
  # create-check-run:
  # create-code-scanning-alert:
  # create-discussion:
  # create-project:
  # create-project-status-update:
  # create-pull-request:
  # create-pull-request-review-comment:
  # dismiss-pull-request-review:
  # dismiss-review:
  # dispatch-repository:
  # dispatch-workflow:
  # dispatch_repository:
  # environment:
  # failure-issue-repo:
  # group-reports:
  # hide-comment:
  # id-token:
  # jira-add-comment:
  # jira-add-label:
  # jira-create-issue:
  # jira-update-issue:
  # linear-add-comment:
  # linear-create-issue:
  # linear-token:
  # linear-update-issue:
  # link-sub-issue:
  # mark-pull-request-as-ready-for-review:
  # max-bot-mentions:
  # max-patch-files:
  # mentions:
  # merge-pull-request:
  # missing-data:
  # missing-tool:
  # noop:
  # push-to-pull-request-branch:
  # remove-labels:
  # replace-label:
  # reply-to-pull-request-review-comment:
  # report-failed-jobs:
  # report-failure-as-issue:
  # report-incomplete:
  # resolve-pull-request-review-thread:
  # scripts:
  # set-issue-field:
  # set-issue-type:
  # steer:
  # steps:
  # submit-pull-request-review:
  # threat-detection:
  # unassign-from-user:
  # update-discussion:
  # update-issue:
  # update-project:
  # update-pull-request:
  # update-release:
  # upload-artifact:
  # upload-asset:
  # upload-code-coverage:
  # urls:

---

# issue-triage

Describe what you want the AI to do when this workflow runs.

## Instructions

Replace this section with specific instructions for the AI. For example:

1. Read the issue description and comments
2. Analyze the request and gather relevant information
3. Provide a helpful response or take appropriate action

Be clear and specific about what the AI should accomplish.

## Notes

- Run `gh aw compile` to generate the GitHub Actions workflow
- See https://github.github.com/gh-aw/ for complete configuration options and tools documentation
