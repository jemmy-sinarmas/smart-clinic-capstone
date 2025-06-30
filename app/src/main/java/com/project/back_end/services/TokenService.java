package com.project.back_end.services;

@Component
public class TokenService {
    private final String SECRET = "SecretKeyForJwtSigning12345678901234567890";
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private PatientRepository patientRepository;

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(7, ChronoUnit.DAYS)))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token, String user) {
        try {
            String email = extractEmail(token);
            return switch (user.toLowerCase()) {
                case "admin" -> adminRepository.findByUsername(email).isPresent();
                case "doctor" -> doctorRepository.findByEmail(email).isPresent();
                case "patient" -> patientRepository.findByEmail(email).isPresent();
                default -> false;
            };
        } catch (Exception e) { return false; }
    }

    private Key getSigningKey() {
        byte[] keyBytes = SECRET.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
