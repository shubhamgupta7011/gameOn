## GAMEON&#x20;

### Project Overview:

GAMEON is a social app that connects users based on shared skills like sports, music, and dancing. It enables users to meet, chat, organize dates, and create connections based on common interests. Key features include:

- **Skill-based Matching:** Users are matched based on common skills and activities such as sports, dancing, music, and more.
- **Chat & Meet:** Users can chat with their matches and even organize dates directly through the app.
- **Friend Connections:** Users can add their matches as friends to build stronger connections.
- **Ranking System:** A user ranking mechanism based on skills and security rating.
- **SOS Safety System:** An emergency SOS system for user safety during events or meetups.
- **Event Creation & Booking:** Users can create events to meet people at specific venues and amenities listed on the app. These bookings can be managed directly through the app.
- **Private and Public Events:**
    - *Private Events*: Users can create private events with people from their matched list or connection list.
    - *Public Events*: Public events such as music concerts, sports tournaments, and community gatherings are created by public forums/clubs or the app itself. Users can join these events seamlessly.

---

### Branching Strategy:

1. **Main (master)** - Contains production-ready code. Only stable releases are merged here.
2. **Develop** - Active development branch. Features and fixes are merged here for integration and testing.
3. **Feature Branches (feature/...)** - For individual features or enhancements. Example:
    - `feature/chat-system`
    - `feature/event-booking`
    - `feature/sos-implementation`
    - `feature/ranking-system`
4. **Bugfix Branches (bugfix/...)** - For critical bug fixes. Example:
    - `bugfix/login-issue`
    - `bugfix/amenity-booking`
5. **Hotfix Branches (hotfix/...)** - For emergency fixes directly applied to `main`. Example:
    - `hotfix/sos-button-crash`

---

### Workflow:

1. Checkout to `develop`:

   ```bash
   git checkout develop
   git pull origin develop
   ```

2. Create a new branch:

   ```bash
   git checkout -b feature/your-feature-name
   ```

3. Work on your feature and commit regularly:

   ```bash
   git add .
   git commit -m "[Feature] Added chat interface"
   ```

4. Push your branch to remote:

   ```bash
   git push origin feature/your-feature-name
   ```

5. Open a Pull Request (PR) to `develop` branch on GitHub.

6. Code Review and Testing:

    - Ensure your code passes all tests.
    - Resolve merge conflicts if any.

7. Merge PR to `develop` if approved.

8. After successful testing on `develop`, create a PR to `main` for release.

9. Hotfixes can be directly branched from `main`, merged back to `main`, and `develop`.

---

### Release Management:

- Versioning follows `vX.Y.Z` format (e.g., v1.0.0)
- Tags are created on `main` during release:
  ```bash
  git tag v1.0.0
  git push origin v1.0.0
  ```

---

### Best Practices:

1. Commit frequently with clear messages.
2. Keep feature branches focused—one feature per branch.
3. Always pull the latest changes before starting work.
4. Resolve conflicts before PR submission.
5. Review PRs thoroughly before merging.

---

### Future Enhancements:

- Automated CI/CD integration.
- Linting and format checks before PR approval.
- Security checks for vulnerabilities during the build process.

