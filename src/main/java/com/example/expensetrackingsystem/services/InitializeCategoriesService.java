package com.example.expensetrackingsystem.services;

import com.example.expensetrackingsystem.entities.Category;
import com.example.expensetrackingsystem.entities.CategoryEnum;
import com.example.expensetrackingsystem.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class InitializeCategoriesService implements CommandLineRunner {

    CategoryRepository categoryRepository;

    @Autowired
    public InitializeCategoriesService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        if(categoryRepository.count() == 0) {

            for(CategoryEnum entity : CategoryEnum.values() ) {

                Category category = new Category();
                category.setId(entity.ordinal());
                category.setCategoryName(entity.name());

                CategoryEnum parent = entity.getParent();

                if(parent != null) {
                    Optional<Category> parentCategory = categoryRepository.findByCategoryName(parent.name());
                    if (parentCategory.isPresent()) {
                        category.setParentCategory(parentCategory.get());
                    } else {
                        category.setParentCategory(null);
                    }
                }
                categoryRepository.save(category);
            }
            System.out.println("Finished Initializing categories");
        }
        else{
            System.out.println("Categories already initialized");
        }

    }


}
