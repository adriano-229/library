# 🔐 3-Layer Security Implementation

This document explains the **3-layer security approach** implemented in the Library Management System.

---

## 📋 Security Requirements

### 👑 ROLE_ADMIN

- Full access to **all 6 features**:
    - 📚 Books
    - ✍️ Authors
    - 🏢 Publishers
    - 📖 Loans
    - 👥 Users
    - 🔑 Change Password

### 👤 ROLE_USER

- Limited access to **2 features only**:
    - 📖 Loans (can only create loans for themselves)
    - 🔑 Change Password (can only change their own password)

---

## 🛡️ Three-Layer Security Defense

### **Layer 1: FilterChain (URL-based security)** 🚪

**File:** `SecurityConfig.java`

The **first barrier** - controls which URLs are accessible based on role.

```java
.authorizeHttpRequests(auth ->auth
        .

requestMatchers("/css/**","/js/**","/login").

permitAll()
// ADMIN-only endpoints
    .

requestMatchers("/books/**","/authors/**","/publishers/**","/users/**","/uploads/**").

hasRole("ADMIN")
// USER can access loans and change password
    .

requestMatchers("/loans/**","/change-password").

hasAnyRole("USER","ADMIN")
// other requests require authentication
    .

anyRequest().

authenticated()
)
```

**Why at FilterChain level?**

- ✅ First line of defense - blocks unauthorized requests **before** they reach controllers
- ✅ Simple and centralized URL pattern matching
- ✅ Prevents direct URL access attempts (e.g., typing `/books` as a USER)

---

### **Layer 2: Method Security (Business logic protection)** 🔒

**Files:** Service classes (`BookService.java`, `AuthorService.java`, `LoanService.java`, etc.)

The **second barrier** - protects business operations using `@PreAuthorize`.

#### Why at Service level instead of Controller?

**Services** are the best place because:

1. 🎯 **Single point of enforcement** - controllers might call services directly or through other paths
2. 🧩 **Reusability** - if you add a REST API later, the same service security applies
3. 🛡️ **Defense in depth** - even if someone bypasses the controller, service is still protected

#### Implementation Examples

**ADMIN-only services:**

```java

@Service
public class BookService extends BaseService<Book, Long> {

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Book save(Book entity) {
        return super.save(entity);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Book update(Long id, Book entity) {
        return super.update(id, entity);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(Long id) {
        super.deleteById(id);
    }
}
```

**USER + ADMIN with custom logic (LoanService):**

```java

@Service
public class LoanService extends BaseService<Loan, Long> {

    @Override
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Loan save(Loan loan) {
        // 🔍 Custom security: USER can only create loans for themselves
        if (!isAdmin()) {
            String currentUserEmail = getCurrentUserEmail();
            User currentUser = userService.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new IllegalStateException("Current user not found"));

            if (loan.getUser() == null || !loan.getUser().getId().equals(currentUser.getId())) {
                throw new SecurityException("You can only create loans for yourself");
            }
        }
        return super.save(loan);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Loan update(Long id, Loan loan) {
        return super.update(id, loan);
    }
}
```

**Controllers also have @PreAuthorize:**

```java

@Controller
@RequestMapping("/books")
@PreAuthorize("hasRole('ADMIN')")
public class BookController extends BaseController<Book> {
    // All methods in this controller require ADMIN
}
```

---

### **Layer 3: View Security (UI restrictions)** 👁️

**Files:** Thymeleaf templates (`home.html`, `loans/form.html`, etc.)

The **third barrier** - hides UI elements users shouldn't see using `sec:authorize`.

#### Home Page Cards

```html
<!-- ADMIN-only cards -->
<div class="col-md-4 mb-4" sec:authorize="hasRole('ADMIN')">
  <a href="/books" class="feature-card">
    <i class="bi bi-book-fill feature-icon"></i>
    <h4 class="feature-title">Books</h4>
  </a>
</div>

<!-- USER + ADMIN cards -->
<div class="col-md-4 mb-4" sec:authorize="hasAnyRole('USER', 'ADMIN')">
  <a href="/loans" class="feature-card">
    <i class="bi bi-arrow-left-right feature-icon"></i>
    <h4 class="feature-title">Loans</h4>
  </a>
</div>
```

#### Loan Form - Dynamic User Selection

**ADMIN** sees a dropdown to select any user:

```html
<select sec:authorize="hasRole('ADMIN')" id="user" th:field="*{user.id}" class="form-select" required>
  <option value="">Select a user...</option>
  <option th:each="user : ${users}" th:value="${user.id}" th:text="${user.email}"></option>
</select>
```

**USER** sees only themselves (read-only):

```html
<select sec:authorize="hasRole('USER') and !hasRole('ADMIN')" id="user" th:field="*{user.id}"
        class="form-select" required disabled>
  <option th:each="user : ${users}" th:value="${user.id}" th:text="${user.email}"
          th:selected="${user.email == #authentication.name}"></option>
</select>
<input sec:authorize="hasRole('USER') and !hasRole('ADMIN')" type="hidden"
       th:field="*{user.id}" th:value="${currentUserId}"/>
```

**Why at View level?**

- ✅ **Better UX** - users don't see options they can't use
- ✅ **Reduces confusion** - cleaner interface tailored to role
- ✅ **Not a security measure alone** - just UI polish (real security is in Layers 1 & 2)

---

## 🎯 Summary: Why 3 Layers?

| Layer                  | Location               | Purpose                | Can be bypassed?                                    |
|------------------------|------------------------|------------------------|-----------------------------------------------------|
| **1. FilterChain**     | `SecurityConfig.java`  | Block URLs             | ❌ No - Spring Security enforces                     |
| **2. Method Security** | Services + Controllers | Protect business logic | ❌ No - `@PreAuthorize` enforced                     |
| **3. View Security**   | Thymeleaf templates    | Hide UI elements       | ⚠️ Yes - but attacker still blocked by Layers 1 & 2 |

### 🔐 Defense in Depth Strategy

Even if an attacker:

- 🚫 Tries to access `/books` directly → **Blocked by Layer 1** (FilterChain)
- 🚫 Somehow calls `bookService.save()` → **Blocked by Layer 2** (Method Security)
- 🚫 Manipulates HTML to show hidden buttons → **Still blocked by Layers 1 & 2**

---

## 🧪 Testing the Security

### As ADMIN user:

1. ✅ Login → See all 6 cards
2. ✅ Can create/edit books, authors, publishers, users
3. ✅ Can create loans for **any user**
4. ✅ Can edit/delete any loan
5. ✅ Can change own password

### As USER user:

1. ✅ Login → See only 2 cards (Loans, Change Password)
2. ❌ Cannot access `/books`, `/authors`, `/publishers`, `/users` (403 Forbidden)
3. ✅ Can create loans **only for themselves** (user dropdown is pre-filled and disabled)
4. ❌ Cannot edit or delete loans (buttons hidden, endpoints protected)
5. ✅ Can change own password

---

## 📝 Files Modified

### Configuration

- ✅ `SecurityConfig.java` - FilterChain rules

### Services (Method Security)

- ✅ `BookService.java`
- ✅ `AuthorService.java`
- ✅ `PublisherService.java`
- ✅ `UserService.java`
- ✅ `LoanService.java` (+ custom logic for USER self-loans)

### Controllers (Method Security)

- ✅ `BookController.java`
- ✅ `AuthorController.java`
- ✅ `PublisherController.java`
- ✅ `UserController.java`
- ✅ `LoanController.java` (+ auto-set current user for non-admin)
- ✅ `ChangePasswordController.java` (already restricts to current user)

### Views (UI Security)

- ✅ `home.html` - Role-based card visibility
- ✅ `loans/form.html` - Dynamic user selector based on role

---

## 🚀 Next Steps (Optional Enhancements)

1. **Audit Logging** 📊 - Log who does what (especially ADMIN actions)
2. **Rate Limiting** ⏱️ - Prevent brute force attacks on login
3. **CSRF Protection** 🛡️ - Already enabled by default in prod profile
4. **Password Policies** 🔑 - Enforce stronger passwords (min length, complexity)
5. **Session Management** ⏰ - Auto-logout after inactivity
6. **Two-Factor Authentication** 📱 - Extra security layer

---

**Made with ❤️ and 🔐**

