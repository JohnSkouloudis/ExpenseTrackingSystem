package com.example.expensetrackingsystem.entities;

public enum CategoryEnum {

   // Main categories
    INCOME(null),
    EXPENSE(null),


    SALARY(INCOME),
    FREELANCE(INCOME),
    INVESTMENTS(INCOME),


    FOOD(EXPENSE),
    GROCERIES(FOOD),
    DINING_OUT(FOOD),
    COFFEE_SNACKS(FOOD),
    TRANSPORT(EXPENSE),
    FUEL(TRANSPORT),
    PUBLIC_TRANSPORT(TRANSPORT);

    private final CategoryEnum parent;

    CategoryEnum(CategoryEnum parent) {
        this.parent = parent;
    }

    public CategoryEnum getParent() {
        return parent;
    }

    public static  boolean isCategoryExpense(int categoryId) {

       CategoryEnum current =  CategoryEnum.values()[categoryId];

       while (current != null) {

         if (current == EXPENSE) {
            return true;
        }
         current = current.getParent();
       }
       return false;

  }



}
