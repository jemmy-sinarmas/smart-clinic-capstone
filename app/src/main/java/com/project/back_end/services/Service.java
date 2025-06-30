package com.project.back_end.services;

@Service
public class Service {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private PatientService patientService;

    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        if (tokenService.validateToken(token, user)) return ResponseEntity.ok(Map.of("message", "Valid"));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or expired token"));
    }

    public ResponseEntity<Map<String, String>> validateAdmin(Admin admin) {
        Optional<Admin> found = adminRepository.findByUsername(admin.getUsername());
        if (found.isPresent() && found.get().getPassword().equals(admin.getPassword())) {
            return ResponseEntity.ok(Map.of("token", tokenService.generateToken(admin.getUsername())));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid credentials"));
        }
    }

    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        return doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
    }

    public int validateAppointment(Appointment appointment) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(appointment.getDoctorId());
        if (doctorOpt.isEmpty()) return -1;
        List<String> available = doctorService.getDoctorAvailability(appointment.getDoctorId(), appointment.getAppointmentTime().toLocalDate());
        return available.contains(appointment.getAppointmentTime().toLocalTime().toString()) ? 1 : 0;
    }

    public boolean validatePatient(Patient patient) {
        return patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone()).isEmpty();
    }

    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Optional<Patient> patientOpt = patientRepository.findByEmail(login.getEmail());
        if (patientOpt.isPresent() && patientOpt.get().getPassword().equals(login.getPassword())) {
            return ResponseEntity.ok(Map.of("token", tokenService.generateToken(login.getEmail())));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid credentials"));
        }
    }

    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
        String email = tokenService.extractEmail(token);
        Optional<Patient> patientOpt = patientRepository.findByEmail(email);
        if (patientOpt.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid token"));
        Patient patient = patientOpt.get();
        if (!condition.isBlank() && !name.isBlank()) return patientService.filterByDoctorAndCondition(condition, name, patient.getId());
        else if (!condition.isBlank()) return patientService.filterByCondition(condition, patient.getId());
        else if (!name.isBlank()) return patientService.filterByDoctor(name, patient.getId());
        else return ResponseEntity.ok(Map.of("appointments", List.of()));
    }
}
