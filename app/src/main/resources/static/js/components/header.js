function renderHeader() {
  const headerDiv = document.getElementById("header");
  if (window.location.pathname.endsWith("/")) {
    localStorage.removeItem("userRole");
    localStorage.removeItem("token");
  }

  const role = localStorage.getItem("userRole");
  const token = localStorage.getItem("token");

  if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
    localStorage.removeItem("userRole");
    alert("Session expired or invalid login. Please log in again.");
    window.location.href = "/";
    return;
  }

  let headerContent = "";
  if (role === "admin") {
    headerContent += `<button id="addDocBtn" class="adminBtn" onclick="openModal('addDoctor')">Add Doctor</button><a href="#" onclick="logout()">Logout</a>`;
  } else if (role === "doctor") {
    headerContent += `<a href="/doctorDashboard.html">Home</a><a href="#" onclick="logout()">Logout</a>`;
  } else if (role === "patient") {
    headerContent += `<a href="/login.html">Login</a><a href="/signup.html">Sign Up</a>`;
  } else if (role === "loggedPatient") {
    headerContent += `<a href="/patientDashboard.html">Home</a><a href="/appointments.html">Appointments</a><a href="#" onclick="logoutPatient()">Logout</a>`;
  }

  headerDiv.innerHTML = headerContent;
}

function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("userRole");
  window.location.href = "/";
}

function logoutPatient() {
  localStorage.removeItem("token");
  localStorage.setItem("userRole", "patient");
  window.location.href = "/patientDashboard.html";
}

renderHeader();
