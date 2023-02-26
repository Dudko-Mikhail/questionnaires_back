package by.dudko.questionnaires.web.controller;

import by.dudko.questionnaires.dto.PageResponse;
import by.dudko.questionnaires.dto.field.FieldCreateEditDto;
import by.dudko.questionnaires.dto.field.FieldReadDto;
import by.dudko.questionnaires.service.FieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FieldRestController {
    private final FieldService fieldService;

    @GetMapping("/users/{id}/fields/all")
    public List<FieldReadDto> findAll(@PathVariable long id) { // todo refactor this
        return fieldService.findAllByUserId(id);
    }

    @GetMapping("/users/{id}/fields")
    @PreAuthorize("principal.id == #userId")
    public PageResponse<FieldReadDto> findByUserId(@PathVariable("id") long userId, Pageable pageable) {
        return fieldService.findAllByUserId(userId, pageable);
    }

    @GetMapping("/fields/types")
    public List<String> findAllFieldTypes() {
        return this.fieldService.findAllFieldTypes();
    }

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("/users/{id}/fields")
    @PreAuthorize("principal.id == #userId")
    public FieldReadDto saveField(@PathVariable("id") long userId, @RequestBody @Validated FieldCreateEditDto createEditDto) {
        return fieldService.save(userId, createEditDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/fields/{id}")
    @PreAuthorize("@fieldRepository.isFieldOwner(principal.id, #id)")
    public FieldReadDto updateField(@PathVariable long id, @RequestBody @Validated FieldCreateEditDto createEditDto) {
        return fieldService.update(id, createEditDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @DeleteMapping("/fields/{id}")
    @PreAuthorize("@fieldRepository.isFieldOwner(principal.id, #id)")
    public void deleteById(@PathVariable("id") long id) {
        if (!fieldService.deleteById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
