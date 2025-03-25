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

        return "management/showInventory";
    }

    //shows add inventory form
    @GetMapping("/addInventory")
    public String showAddInventoryForm(Model model) {
        model.addAttribute("inventoryItem", new IngredientDTO());

        return "management/addInventory";
    }

    //shows update inventory form
    @GetMapping("/updateInventory")
    public String showUpdateInventoryForm(@RequestParam("ingredientId") int ingredientId, Model model) {
        model.addAttribute("inventoryItem", menuItemService.findIngredientById(ingredientId));
        model.addAttribute("ingredientId", ingredientId);

        return "management/updateInventory";
    }

    @GetMapping("/deleteInventory")
    public String deleteInventory(@RequestParam("ingredientId") int ingredientId, Model model) {
        // if associated with a menu item, produce an error message
        List<MenuItemIngredient> assocMenuItems = menuItemService.deleteIngredient(ingredientId);
        int listSize = assocMenuItems.size();

        if (listSize > 0) {
            Ingredient ingredient = assocMenuItems.get(0).getIngredient();
            List<Ingredient> inventory = menuItemService.findAllIngredients();
            model.addAttribute("assocMenuItemsErr",
                    "Cannot delete " + ingredient.getIngredientName() + " it is associated with " + listSize + " menu item(s)");
            model.addAttribute("inventory", inventory);
            return "management/showInventory";
        }

        return "redirect:/system/inventory/showInventory";
    }

    @PostMapping("/saveInventory")
    public String saveInventory(@Valid @ModelAttribute("inventoryItem") IngredientDTO ingredientDTO,
                                BindingResult theBindingResult, Model model) {

        if (theBindingResult.hasErrors()) {
            model.addAttribute("inventoryError", "You must correct the errors before proceeding");
            model.addAttribute("inventoryItem", ingredientDTO);

            return "management/addInventory";
        }
        else if (menuItemService.findIngredientByName(ingredientDTO.getIngredientName()) != null) {
            model.addAttribute("inventoryError", "Ingredient already exists with this name");
            model.addAttribute("inventoryItem", ingredientDTO);

            return "management/addInventory";
        }

        menuItemService.saveIngredient(ingredientDTO);

        return "redirect:/system/inventory/showInventory";
    }

    // thymeleaf gets ingredientId from model and sends with form data as requestParam
    @PostMapping("/saveUpdatedInventory")
    public String updateInventory(@RequestParam("ingredientId") int ingredientId,
                                  @Valid @ModelAttribute("inventoryItem") IngredientDTO ingredientDTO,
                                  BindingResult theBindingResult, Model model) {

        if (theBindingResult.hasErrors()) {
            model.addAttribute("inventoryError", "You must correct the errors before proceeding");
            model.addAttribute("inventoryItem", ingredientDTO);
            model.addAttribute("ingredientId", ingredientId);

            return "management/updateInventory";
        }

        menuItemService.updateIngredient(ingredientId, ingredientDTO);

        return "redirect:/system/inventory/showInventory";
    }
}
