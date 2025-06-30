package com.project.back_end.services;

@Service
public class PrescriptionService {
    @Autowired
    private PrescriptionRepository prescriptionRepository;

    public ResponseEntity<Map<String, String>> savePrescription(Prescription prescription) {
        try {
            prescriptionRepository.save(prescription);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Prescription saved"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error saving prescription"));
        }
    }

    public ResponseEntity<Map<String, Object>> getPrescription(Long appointmentId) {
        Optional<Prescription> prescription = prescriptionRepository.findByAppointmentId(appointmentId);
        return prescription.map(value -> ResponseEntity.ok(Map.of("prescription", value)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "No prescription found")));
    }
}
