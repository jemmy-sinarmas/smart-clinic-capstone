
package com.project.back_end.controllers;

@RestController
@RequestMapping("${api.path}" + "admin")
public class AdminController {

    @Autowired
    private Service service;

    @PostMapping
    public ResponseEntity<Map<String, String>> adminLogin(@RequestBody Admin admin) {
        return service.validateAdmin(admin);
    }
}
