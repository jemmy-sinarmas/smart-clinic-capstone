package com.project.back_end.services;

@Service
public class DoctorService {
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private TokenService tokenService;

    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        List<String> allSlots = List.of("09:00", "10:00", "11:00", "14:00", "15:00");
        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
            doctorId,
            date.atStartOfDay(),
            date.plusDays(1).atStartOfDay()
        );
        List<String> bookedSlots = appointments.stream()
            .map(a -> a.getAppointmentTime().toLocalTime().toString())
            .collect(Collectors.toList());
        return allSlots.stream().filter(slot -> !bookedSlots.contains(slot)).collect(Collectors.toList());
    }

    public int saveDoctor(Doctor doctor) {
        if (doctorRepository.findByEmail(doctor.getEmail()).isPresent()) return -1;
        try { doctorRepository.save(doctor); return 1; } catch (Exception e) { return 0; }
    }

    public int updateDoctor(Doctor doctor) {
        Optional<Doctor> existing = doctorRepository.findById(doctor.getId());
        if (existing.isEmpty()) return -1;
        try { doctorRepository.save(doctor); return 1; } catch (Exception e) { return 0; }
    }

    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    public int deleteDoctor(long id) {
        Optional<Doctor> doctor = doctorRepository.findById(id);
        if (doctor.isEmpty()) return -1;
        try {
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) { return 0; }
    }

    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Optional<Doctor> doctorOpt = doctorRepository.findByEmail(login.getEmail());
        if (doctorOpt.isPresent() && doctorOpt.get().getPassword().equals(login.getPassword())) {
            String token = tokenService.generateToken(login.getEmail());
            return ResponseEntity.ok(Map.of("token", token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid credentials"));
        }
    }
}
