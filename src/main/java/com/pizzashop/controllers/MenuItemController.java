package com.pizzashop.controllers;

import com.pizzashop.dto.MenuItemDTO;
import com.pizzashop.entities.MenuCategoryEnum;
import com.pizzashop.entities.MenuItem;
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

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/system/changeMenu")
public class MenuItemController {

    MenuItemService menuItemService;

    @Autowired
    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/showMenuItems")
    public String showMenuItems(Model model) {
        List<MenuItem> menuItems = menuItemService.findAllMenuItems();
        model.addAttribute("menuItems", menuItems);
        model.addAttribute("heading", "All Menu Items");
        return "management/showMenuItems";
    }

    @GetMapping("/showMenuItemRecipe")
    public String showMenuItemRecipe(@RequestParam("menuItemId") int menuItemId,
                                     @RequestParam("menuItemName") String menuItemName, Model model) {
        MenuItem menuItem = menuItemService.findMenuItemById(menuItemId);
        List<String[]> menuItemRecipe = buildRecipeByMenuItem(menuItem);

        model.addAttribute("menuItemRecipe", menuItemRecipe);
        model.addAttribute("menuItemName", menuItemName);
        model.addAttribute("menuItemId", menuItemId);
        model.addAttribute("heading", menuItemName + " recipe");

        return "management/showMenuItemRecipe";
    }

    @GetMapping("/addMenuItem")
    public String showAddMenuItemForm(Model model) {
        model.addAttribute("menuItem", new MenuItemDTO());
        model.addAttribute("categories", MenuCategoryEnum.values());
        model.addAttribute("ingredients", menuItemService.findAllIngredients());
        model.addAttribute("heading", "Create A New Menu Item");

        return "management/addMenuItem";
    }

    @GetMapping("/updateMenuItem")
    public String showUpdateMenuItemForm(@RequestParam("menuItemId") int menuItemId, Model model) {
        MenuItem menuItem = menuItemService.findMenuItemById(menuItemId);
        List<String[]> menuItemRecipe = buildRecipeByMenuItem(menuItem);

        model.addAttribute("menuItem", menuItem);
        model.addAttribute("ingredients", menuItemService.findAllIngredients());
        model.addAttribute("menuItemId", menuItemId);
        model.addAttribute("categories", MenuCategoryEnum.values());
        model.addAttribute("heading", "Update " + menuItem.getDishName());
        model.addAttribute("menuItemRecipe", menuItemRecipe);

        return "management/updateMenuItem";
    }

    @GetMapping("/deleteMenuItem")
    public String deleteMenuItem(@RequestParam("menuItemId") int menuItemId) {
        menuItemService.deleteMenuItem(menuItemId);

        return "redirect:/system/changeMenu/showMenuItems";
    }

    @PostMapping("/saveMenuItem")
    public String saveMenuItem(@Valid @ModelAttribute("menuItem") MenuItemDTO menuItemDTO, BindingResult theBindingResult, Model model,
                               @RequestParam("ingredientIdAmountsKeys") Integer[] ingredientIdAmountsKeys,
                               @RequestParam("ingredientIdAmountsValues") Integer[] ingredientIdAmountValues) {

        String errorMsg = "";

        if (theBindingResult.hasErrors()) {
            errorMsg = "You must correct the errors before proceeding";
        } else if (menuItemService.findIngredientByName(menuItemDTO.getDishName()) != null) {
            errorMsg = "Menu item already exists with this name";
        } else if (ingredientIdAmountsKeys.length == 0 || ingredientIdAmountValues.length == 0 || ingredientIdAmountsKeys.length != ingredientIdAmountValues.length) {
            errorMsg = "Ingredients must have associated quantities";
        }

        if (errorMsg.isEmpty()) {
            List<int[]> ingredientIdsQty = new ArrayList<>();
            for(int i = 0; i < ingredientIdAmountsKeys.length; i++){
                ingredientIdsQty.add(new int[]{ingredientIdAmountsKeys[i], ingredientIdAmountValues[i]});
            }
            menuItemDTO.setIngredientIdAmounts(ingredientIdsQty);
            menuItemService.saveMenuItem(menuItemDTO);
            return "redirect:/system/changeMenu/showMenuItems";
        } else {
            model.addAttribute("menuItemError", errorMsg);
            model.addAttribute("menuItem", menuItemDTO);
            model.addAttribute("ingredients", menuItemService.findAllIngredients());
            model.addAttribute("categories", MenuCategoryEnum.values());
            model.addAttribute("heading", "Create A New Menu Item");
            return "management/addMenuItem";
        }
    }

    @PostMapping("/updateMenuItem")
    public String updateMenuItem(@Valid @ModelAttribute("menuItem") MenuItemDTO menuItemDTO, BindingResult theBindingResult, Model model,
                                 @RequestParam("menuItemId") int menuItemId,
                                 @RequestParam("ingredientIdAmountsKeys") Integer[] ingredientIdAmountsKeys,
                                 @RequestParam("ingredientIdAmountsValues") Integer[] ingredientIdAmountValues) {

        String errorMsg = "";

        if (theBindingResult.hasErrors()) {
            errorMsg = "You must correct the errors before proceeding";
        } else if (menuItemService.findIngredientByName(menuItemDTO.getDishName()) != null) {
            errorMsg = "Menu item already exists with this name";
        } else if (ingredientIdAmountsKeys.length == 0 || ingredientIdAmountValues.length == 0 || ingredientIdAmountsKeys.length != ingredientIdAmountValues.length) {
            errorMsg = "Ingredients must have associated quantities";
        }

        if (errorMsg.isEmpty()) {
            List<int[]> ingredientIdsQty = new ArrayList<>();
            for(int i = 0; i < ingredientIdAmountsKeys.length; i++){
                ingredientIdsQty.add(new int[]{ingredientIdAmountsKeys[i], ingredientIdAmountValues[i]});
            }
            menuItemDTO.setIngredientIdAmounts(ingredientIdsQty);
            menuItemService.saveMenuItem(menuItemDTO);
            return "redirect:/system/changeMenu/showMenuItems";
        } else {
            MenuItem menuItem = menuItemService.findMenuItemById(menuItemId);
            List<String[]> menuItemRecipe = buildRecipeByMenuItem(menuItem);
            model.addAttribute("menuItemError", errorMsg);
            model.addAttribute("menuItem", menuItemDTO);
            model.addAttribute("ingredients", menuItemService.findAllIngredients());
            model.addAttribute("categories", MenuCategoryEnum.values());
            model.addAttribute("heading", "Update " + menuItem.getDishName());
            model.addAttribute("menuItemRecipe", menuItemRecipe);
            return "management/updateMenuItem";
        }
    }

    private List<String[]> buildRecipeByMenuItem(MenuItem menuItem) {
        List<String[]> menuItemRecipe = new ArrayList<>();
        List<MenuItemIngredient> ingredients = menuItem.getMenuItemIngredients();
        for (MenuItemIngredient menuItemIngredient : ingredients) {
            String menuItemQuantityWithUnit = menuItemIngredient.getQuantityUsed() + " " + menuItemIngredient.getIngredient().getUnitOfMeasure();
            menuItemRecipe.add(new String[]{menuItemIngredient.getIngredient().getIngredientName(), menuItemQuantityWithUnit});
        }
        return menuItemRecipe;
    }

}