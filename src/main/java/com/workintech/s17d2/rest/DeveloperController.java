package com.workintech.s17d2.rest;

import com.workintech.s17d2.model.*;
import com.workintech.s17d2.tax.DeveloperTax;
import com.workintech.s17d2.tax.Taxable;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DeveloperController {
    private Map<Integer, Developer> developers = new HashMap<>();
    private Taxable developerTax;

    @PostConstruct
    public void init() {
        developers.put(1, new SeniorDeveloper(1, "tayfun", 30000));
        developers.put(2, new MidDeveloper(2, "umut", 20000));
        developers.put(3, new JuniorDeveloper(3, "mete", 44444));
    }

    @Autowired
    public DeveloperController(Taxable developerTax) {
        this.developerTax = developerTax;
    }

    @GetMapping("/developers")
    public List<Developer> getDevelopers() {
        return developers.values().stream().toList();
    }

    @GetMapping("/developers/{id}")
    public Developer getDeveloperById(@PathVariable int id) {
        return developers.get(id);
    }

    @PostMapping("/developers")
    public ResponseEntity<Developer> addDeveloper(@RequestBody Developer developer) {
        Developer dev = null;
        if (developer.getExperience() == Experience.SENIOR) {
            dev = new SeniorDeveloper(developer.getId(), developer.getName(), developer.getSalary() - developer.getSalary() * developerTax.getUpperTaxRate());
        } else if (developer.getExperience() == Experience.MID) {
            dev = new MidDeveloper(developer.getId(), developer.getName(), developer.getSalary() - developer.getSalary() * developerTax.getMiddleTaxRate());
        } else if (developer.getExperience() == Experience.JUNIOR) {
            dev = new JuniorDeveloper(developer.getId(), developer.getName(), developer.getSalary() - developer.getSalary() * developerTax.getSimpleTaxRate());
        }
        if (dev != null) {
            developers.put(developer.getId(), dev);
            return new ResponseEntity<>(dev, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/developers/{id}")
    public Developer updateDev(@PathVariable int id, @RequestBody Developer developer) {
        developers.put(id, developer);
        return developer;
    }

    @DeleteMapping("/developers/{id}")
    public Developer deleteDev(@PathVariable int id) {
        Developer developer = developers.get(id);
        developers.remove(id);
        return developer;
    }
}
