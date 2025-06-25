export function createDoctorCard(doctor) {
  const card = document.createElement("div");
  card.classList.add("doctor-card");

  const role = localStorage.getItem("userRole");

  const infoDiv = document.createElement("div");
  infoDiv.classList.add("doctor-info");

  const name = document.createElement("h3");
  name.textContent = doctor.name;
  const specialization = document.createElement("p");
  specialization.textContent = `Specialty: ${doctor.specialty}`;
  const email = document.createElement("p");
  email.textContent = `Email: ${doctor.email}`;
  const availability = document.createElement("p");
  availability.textContent = `Available: ${doctor.available_times.join(", ")}`;

  infoDiv.append(name, specialization, email, availability);

  const actionsDiv = document.createElement("div");
  actionsDiv.classList.add("card-actions");

  if (role === "admin") {
    const removeBtn = document.createElement("button");
    removeBtn.textContent = "Delete";
    removeBtn.addEventListener("click", async () => {
      const confirmed = confirm("Are you sure you want to delete this doctor?");
      if (confirmed) {
        const token = localStorage.getItem("token");
        await deleteDoctor(doctor.id, token);
        card.remove();
      }
    });
    actionsDiv.appendChild(removeBtn);
  } else if (role === "patient") {
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";
    bookNow.addEventListener("click", () => alert("Please login to book an appointment."));
    actionsDiv.appendChild(bookNow);
  } else if (role === "loggedPatient") {
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";
    bookNow.addEventListener("click", async (e) => {
      const token = localStorage.getItem("token");
      const patientData = await getPatientData(token);
      showBookingOverlay(e, doctor, patientData);
    });
    actionsDiv.appendChild(bookNow);
  }

  card.append(infoDiv, actionsDiv);
  return card;
}
