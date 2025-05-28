package com.pizzashop.controllers;

import com.pizzashop.dto.IngredientDTO;
import com.pizzashop.entities.Ingredient;
import com.pizzashop.entities.MenuItemIngredient;
import com.pizzashop.services.MenuItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/system/inventory")
public class InventoryController {

    MenuItemService menuItemService;

    @Autowired
    public InventoryController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/showInventory")
    public String showInventory(Model model) {
        List<Ingredient> inventory = menuItemService.findAllIngredients();
        model.addAttribute("inventory", inventory);
        model.addAttribute("heading", "Inventory Management");
        model.addAttribute("secondaryHeading", "");
        model.addAttribute("pageTitle", "Inventory Management");
        model.addAttribute("additionalStyles", Arrays.asList("/styles/tables.css"));

        return "management/showInventory";
    }

    //shows add/update inventory form depending on ingredientId
    @GetMapping("/addInventory")
    public String showAddInventoryForm(Model model, @RequestParam(value = "ingredientId", required = false) Integer ingredientId) {
        model.addAttribute("additionalStyles", Arrays.asList("/styles/forms.css"));
        model.addAttribute("heading", "Inventory Management");
        model.addAttribute("secondaryHeading", "");
        model.addAttribute("pageTitle", "Inventory Management");

        if (ingredientId == null) {
            model.addAttribute("inventoryItem", new IngredientDTO());
            model.addAttribute("addType", "New Ingredient");
        } else {
            Ingredient ingredient = menuItemService.findIngredientById(ingredientId);
            model.addAttribute("inventoryItem", ingredient);
            model.addAttribute("ingredientId", ingredientId);
            model.addAttribute("addType", "Update " + ingredient.getIngredientName());
        }

        return "management/addInventory";
    }

    @GetMapping("/deleteInventory")
    public String deleteInventory(@RequestParam("ingredientId") int ingredientId, Model model) {
        int assocMenuItems = menuItemService.deleteIngredient(ingredientId);

        if (assocMenuItems > 0) {
            List<Ingredient> inventory = menuItemService.findAllIngredients();
            model.addAttribute("assocMenuItemsErr",
                    "Cannot delete this ingredient, it is associated with " + assocMenuItems + " item(s)");
            model.addAttribute("inventory", inventory);
            return "management/showInventory";
        }

        return "redirect:/system/inventory/showInventory";
    }

    @PostMapping("/saveInventory")
    public String saveInventory(@Valid @ModelAttribute("inventoryItem") IngredientDTO ingredientDTO,
                                BindingResult theBindingResult, Model model,
                                @RequestParam(value = "ingredientId", required = false) Integer ingredientId) {

        String errorMessage = "";

        if (theBindingResult.hasErrors()) {
            errorMessage = "You must correct the errors before proceeding";
        } else if (ingredientId == null && menuItemService.findIngredientByName(ingredientDTO.getIngredientName()) != null) {
            errorMessage = "Ingredient already exists with this name";
        }

        if (errorMessage.isEmpty()) {
            menuItemService.saveIngredient(ingredientDTO, ingredientId);
            return "redirect:/system/inventory/showInventory";
        } else {

            model.addAttribute("additionalStyles", Arrays.asList("/styles/forms.css"));
            model.addAttribute("heading", "Inventory Management");
            model.addAttribute("secondaryHeading", "");
            model.addAttribute("pageTitle", "Inventory Management");
            if (ingredientId != null) {
                Ingredient ingredient = menuItemService.findIngredientById(ingredientId);
                model.addAttribute("ingredientId", ingredientId);
                model.addAttribute("addType", "Update " + ingredient.getIngredientName());
            } else {
                model.addAttribute("addType", "New Ingredient");
            }
            return "management/addInventory";
        }
    }
}
