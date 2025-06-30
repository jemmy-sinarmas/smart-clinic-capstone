package com.project.back_end.services;

@Service
public class PatientService {
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private TokenService tokenService;

    public int createPatient(Patient patient) {
        try { patientRepository.save(patient); return 1; } catch (Exception e) { return 0; }
    }

    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        String email = tokenService.extractEmail(token);
        Optional<Patient> patientOpt = patientRepository.findByEmail(email);
        if (patientOpt.isEmpty() || !patientOpt.get().getId().equals(id))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));

        List<AppointmentDTO> appts = appointmentRepository.findByPatientId(id)
            .stream().map(AppointmentDTO::from).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("appointments", appts));
    }

    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        int status = condition.equalsIgnoreCase("past") ? 1 : 0;
        List<Appointment> appointments = appointmentRepository.findByPatientIdAndStatus(id, status);
        List<AppointmentDTO> appts = appointments.stream().map(AppointmentDTO::from).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("appointments", appts));
    }

    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long id) {
        List<Appointment> appointments = appointmentRepository.findByPatientIdAndDoctorNameContainingIgnoreCase(id, name);
        List<AppointmentDTO> appts = appointments.stream().map(AppointmentDTO::from).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("appointments", appts));
    }

    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name, long id) {
        int status = condition.equalsIgnoreCase("past") ? 1 : 0;
        List<Appointment> appointments = appointmentRepository.findByPatientIdAndDoctorNameAndStatus(id, name, status);
        List<AppointmentDTO> appts = appointments.stream().map(AppointmentDTO::from).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("appointments", appts));
    }

    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        String email = tokenService.extractEmail(token);
        Optional<Patient> patient = patientRepository.findByEmail(email);
        return patient.map(value -> ResponseEntity.ok(Map.of("patient", value)))
                      .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Patient not found")));
    }
}
