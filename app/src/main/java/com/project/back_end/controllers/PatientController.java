package com.project.back_end.controllers;

@RestController
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private Service service;

    @GetMapping("/{token}")
    public ResponseEntity<?> getPatientDetails(@PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation.getStatusCode().isError()) return validation;
        return patientService.getPatientDetails(token);
    }

    @PostMapping()
    public ResponseEntity<?> createPatient(@RequestBody Patient patient) {
        if (!service.validatePatient(patient))
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Patient with email id or phone no already exist"));
        return patientService.createPatient(patient) == 1 ?
            ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Signup successful")) :
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Internal server error"));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> patientLogin(@RequestBody Login login) {
        return service.validatePatientLogin(login);
    }

    @GetMapping("/{id}/{token}")
    public ResponseEntity<?> getAppointments(@PathVariable Long id, @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation.getStatusCode().isError()) return validation;
        return patientService.getPatientAppointment(id, token);
    }

    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<?> filterAppointments(@PathVariable String condition, @PathVariable String name, @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation.getStatusCode().isError()) return validation;
        return service.filterPatient(condition, name, token);
    }
}
