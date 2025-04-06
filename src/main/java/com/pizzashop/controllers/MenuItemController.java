package com.pizzashop.controllers;

import com.pizzashop.dto.MenuItemDTO;
import com.pizzashop.entities.MenuCategoryEnum;
import com.pizzashop.entities.MenuItem;
import com.pizzashop.entities.OrderMenuItem;
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

        model.addAttribute("menuItemRecipe", menuItemService.buildRecipeByMenuItem(menuItem));
        model.addAttribute("menuItemName", menuItemName);
        model.addAttribute("menuItemId", menuItemId);
        model.addAttribute("heading", menuItemName + " recipe");

        return "management/showMenuItemRecipe";
    }

    @GetMapping("/addMenuItem")
    public String showAddMenuItemForm(Model model, @RequestParam(value = "menuItemId", required = false) Integer menuItemId) {
        model.addAttribute("ingredients", menuItemService.findAllIngredients());
        model.addAttribute("categories", MenuCategoryEnum.values());

        if (menuItemId == null) {
            model.addAttribute("heading", "Create A New Menu Item");
            model.addAttribute("menuItem", new MenuItemDTO());
            model.addAttribute("menuItemId", null);
            model.addAttribute("menuItemRecipe", null);
        } else {
            MenuItem menuItem = menuItemService.findMenuItemById(menuItemId);
            model.addAttribute("heading", "Update " + menuItem.getDishName());
            model.addAttribute("menuItem", menuItem);
            model.addAttribute("menuItemId", menuItemId);
            model.addAttribute("menuItemRecipe", menuItemService.buildRecipeByMenuItem(menuItem));
        }

        return "management/addMenuItem";
    }

    @GetMapping("/deleteMenuItem")
    public String deleteMenuItem(@RequestParam("menuItemId") int menuItemId, Model model) {
        List<OrderMenuItem> associatedOrderAmt =  menuItemService.deleteMenuItem(menuItemId);
        int listSize = associatedOrderAmt.size();

        if (listSize > 0) {
            List<MenuItem> menuItems = menuItemService.findAllMenuItems();
            MenuItem menuItem = associatedOrderAmt.getFirst().getMenuItem();

            model.addAttribute("assocMenuItemsErr",
                    "Cannot delete " + menuItem.getDishName() + " it is associated with " + listSize + " order(s)");
            model.addAttribute("menuItems", menuItems);
            model.addAttribute("heading", "All Menu Items");

            return "management/showMenuItems";
        }

        return "redirect:/system/changeMenu/showMenuItems";
    }

    @PostMapping("/saveMenuItem")
    public String updateMenuItem(@Valid @ModelAttribute("menuItem") MenuItemDTO menuItemDTO, BindingResult theBindingResult, Model model,
                                 @RequestParam(value = "menuItemId", required = false) Integer menuItemId,
                                 @RequestParam("ingredientIdAmountsKeys") Integer[] ingredientIdAmountsKeys,
                                 @RequestParam("ingredientIdAmountsValues") Integer[] ingredientIdAmountValues) {

        String errorMsg = "";

        if (theBindingResult.hasErrors()) {
            errorMsg = "You must correct the errors before proceeding";
        } else if (ingredientIdAmountsKeys.length == 0 || ingredientIdAmountValues.length == 0 || ingredientIdAmountsKeys.length != ingredientIdAmountValues.length) {
            errorMsg = "Ingredients must have associated quantities";
        } else if (menuItemId == null && menuItemService.findMenuItemByName(menuItemDTO.getDishName()) != null) {
            errorMsg = "Menu item already exists with this name";
        }

        if (errorMsg.isEmpty()) {
            menuItemDTO.setIngredientIdAmounts(
                    menuItemService.buildIngredientIdAmounts(ingredientIdAmountsKeys, ingredientIdAmountValues));
            if (menuItemId == null) {
                menuItemService.saveMenuItem(menuItemDTO);
            } else {
                menuItemService.updateMenuItem(menuItemId, menuItemDTO);
            }
            return "redirect:/system/changeMenu/showMenuItems";
        } else {
            model.addAttribute("menuItemError", errorMsg);
            model.addAttribute("ingredients", menuItemService.findAllIngredients());
            model.addAttribute("categories", MenuCategoryEnum.values());
            model.addAttribute("menuItem", menuItemDTO);

            if (menuItemId == null) {
                model.addAttribute("heading", "Create A New Menu Item");
            } else {
                MenuItem menuItem = menuItemService.findMenuItemById(menuItemId);
                List<String[]> menuItemRecipe = menuItemService.buildRecipeByMenuItem(menuItem);
                model.addAttribute("heading", "Update " + menuItem.getDishName());
                model.addAttribute("menuItemRecipe", menuItemRecipe);
            }

            return "management/addMenuItem";
        }
    }

}