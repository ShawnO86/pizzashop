package com.pizzashop.controllers;

import com.pizzashop.dto.MenuItemDTO;
import com.pizzashop.entities.MenuCategoryEnum;
import com.pizzashop.entities.MenuItem;
import com.pizzashop.services.MenuItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Map<String, String> menuItemRecipe = menuItemService.findMenuItemRecipeByMenuId(menuItemId);
        model.addAttribute("menuItemRecipe", menuItemRecipe);
        model.addAttribute("menuItemName", menuItemName);
        model.addAttribute("menuItemId", menuItemId);

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
        Map<String, String> menuItemRecipe = menuItemService.findMenuItemRecipeByMenuId(menuItemId);

        model.addAttribute("menuItem", menuItem);
        model.addAttribute("ingredients", menuItemService.findAllIngredients());
        model.addAttribute("menuItemId", menuItemId);
        model.addAttribute("categories", MenuCategoryEnum.values());
        model.addAttribute("heading", "Update " + menuItem.getDishName());
        model.addAttribute("menuItemRecipe", menuItemRecipe);

        return "management/updateMenuItem";
    }

    @GetMapping("/deleteMenuItem")
    public String deleteMenuItem(@RequestParam("menuItemId") int menuItemId, Model model) {
        menuItemService.deleteMenuItem(menuItemId);

        return "redirect:/system/changeMenu/showMenuItems";
    }

    @PostMapping("/saveMenuItem")
    public String saveMenuItem(@Valid @ModelAttribute("menuItem") MenuItemDTO menuItemDTO, BindingResult theBindingResult, Model model,
                               @RequestParam("ingredientIdAmountsKeys") Integer[] ingredientIdAmountsKeys,
                               @RequestParam("ingredientIdAmountsValues") Integer[] ingredientIdAmountValues) {

        if (theBindingResult.hasErrors()) {
            model.addAttribute("menuItemError", "You must correct the errors before proceeding");
            model.addAttribute("menuItem", menuItemDTO);
            model.addAttribute("ingredients", menuItemService.findAllIngredients());
            model.addAttribute("categories", MenuCategoryEnum.values());
            return "management/addMenuItem";
        }

        if (menuItemService.findIngredientByName(menuItemDTO.getDishName()) != null) {
            model.addAttribute("menuItemError", "Menu item already exists with this name");
            model.addAttribute("menuItem", menuItemDTO);
            model.addAttribute("ingredients", menuItemService.findAllIngredients());
            model.addAttribute("categories", MenuCategoryEnum.values());
            return "management/addMenuItem";
        }

        Map<Integer, Integer> ingredientMap = new HashMap<>();

        if(ingredientIdAmountsKeys.length != 0 && ingredientIdAmountValues.length != 0 && ingredientIdAmountsKeys.length == ingredientIdAmountValues.length){
            for(int i = 0; i < ingredientIdAmountsKeys.length; i++){
                ingredientMap.put(ingredientIdAmountsKeys[i], ingredientIdAmountValues[i]);
            }
        } else {
            model.addAttribute("menuItemError", "Must have amounts with ingredients");
            model.addAttribute("menuItem", menuItemDTO);
            model.addAttribute("ingredients", menuItemService.findAllIngredients());
            model.addAttribute("categories", MenuCategoryEnum.values());
            return "management/addMenuItem";
        }

        menuItemDTO.setIngredientIdAmounts(ingredientMap);

        menuItemService.saveMenuItem(menuItemDTO);

        return "redirect:/system/changeMenu/showMenuItems";
    }

    @PostMapping("/updateMenuItem")
    public String updateMenuItem(@Valid @ModelAttribute("menuItem") MenuItemDTO menuItemDTO, BindingResult theBindingResult, Model model,
                                 @RequestParam("menuItemId") int menuItemId,
                                 @RequestParam("ingredientIdAmountsKeys") Integer[] ingredientIdAmountsKeys,
                                 @RequestParam("ingredientIdAmountsValues") Integer[] ingredientIdAmountValues) {

        if (theBindingResult.hasErrors()) {
            model.addAttribute("menuItemError", "You must correct the errors before proceeding");
            model.addAttribute("menuItem", menuItemDTO);
            model.addAttribute("ingredients", menuItemService.findAllIngredients());
            model.addAttribute("categories", MenuCategoryEnum.values());
            return "management/addMenuItem";
        }

        if (menuItemService.findIngredientByName(menuItemDTO.getDishName()) != null) {
            model.addAttribute("menuItemError", "Menu item already exists with this name");
            model.addAttribute("menuItem", menuItemDTO);
            model.addAttribute("ingredients", menuItemService.findAllIngredients());
            model.addAttribute("categories", MenuCategoryEnum.values());
            return "management/addMenuItem";
        }

        Map<Integer, Integer> ingredientMap = new HashMap<>();

        if(ingredientIdAmountsKeys.length != 0 && ingredientIdAmountValues.length != 0 && ingredientIdAmountsKeys.length == ingredientIdAmountValues.length){
            for(int i = 0; i < ingredientIdAmountsKeys.length; i++){
                ingredientMap.put(ingredientIdAmountsKeys[i], ingredientIdAmountValues[i]);
            }
        } else {
            model.addAttribute("menuItemError", "Must have amounts with ingredients");
            model.addAttribute("menuItem", menuItemDTO);
            model.addAttribute("ingredients", menuItemService.findAllIngredients());
            model.addAttribute("categories", MenuCategoryEnum.values());
            return "management/addMenuItem";
        }

        menuItemDTO.setIngredientIdAmounts(ingredientMap);

        menuItemService.updateMenuItem(menuItemId, menuItemDTO);

        return "redirect:/system/changeMenu/showMenuItems";
    }
}