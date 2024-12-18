package use_case.recipe_search;

import java.util.Arrays;
import java.util.List;

import entity.Recipe;

/**
 * The Search Interactor.
 */
public class RecipeSearchInteractor implements RecipeSearchInputBoundary {
    private final RecipeSearchDataAccessInterface recipeDataAccessObject;
    private final RecipeSearchOutputBoundary recipePresenter;

    public RecipeSearchInteractor(RecipeSearchDataAccessInterface recipeDataAccessObject,
                                  RecipeSearchOutputBoundary recipePresenter) {
        this.recipeDataAccessObject = recipeDataAccessObject;
        this.recipePresenter = recipePresenter;
    }

    @Override
    public void execute(RecipeSearchInputData recipeSearchInputData) {
        final String recipeName = recipeSearchInputData.getRecipeName();
        final String calMin = recipeSearchInputData.getCalMin();
        final String calMax = recipeSearchInputData.getCalMax();
        final String carbMin = recipeSearchInputData.getCarbMin();
        final String carbMax = recipeSearchInputData.getCarbMax();
        final String proteinMin = recipeSearchInputData.getProteinMin();
        final String proteinMax = recipeSearchInputData.getProteinMax();
        final String fatMin = recipeSearchInputData.getFatMin();
        final String fatMax = recipeSearchInputData.getFatMax();
        boolean value = false;

        // Ensure at least one parameter is provided
        if (isAllParametersEmpty(Arrays.asList(recipeName, calMin, calMax, carbMin, carbMax, proteinMin,
                proteinMax, fatMin, fatMax))) {
            recipePresenter.prepareFailView("At least one filter parameter must be provided.");
            value = true;
        }

        // Validate ranges for each parameter
        if (!isValidRange(calMin, calMax)) {
            recipePresenter.prepareFailView("Invalid calorie range: Minimum cannot be greater than maximum.");
            value = true;
        }
        if (!isValidRange(carbMin, carbMax)) {
            recipePresenter.prepareFailView("Invalid carbohydrate range: Minimum cannot be greater than maximum.");
            value = true;
        }
        if (!isValidRange(proteinMin, proteinMax)) {
            recipePresenter.prepareFailView("Invalid protein range: Minimum cannot be greater than maximum.");
            value = true;
        }
        if (!isValidRange(fatMin, fatMax)) {
            recipePresenter.prepareFailView("Invalid fat range: Minimum cannot be greater than maximum.");
            value = true;
        }

        // Fetch recipes using DAO
        final List<Recipe> recipes = recipeDataAccessObject.searchRecipe(recipeName,
                calMin, calMax, carbMin, carbMax, proteinMin, proteinMax, fatMin, fatMax
        );

        if (recipes.isEmpty() && !value) {
            recipePresenter.prepareFailView("No recipes found for the given filters.");
            value = true;
        }
        if (!value) {
            final RecipeSearchOutputData outputData = new RecipeSearchOutputData(recipes, false);
            recipePresenter.prepareSuccessView(outputData);
        }
    }

    // Check if all parameters are empty or null
    private boolean isAllParametersEmpty(List<String> parameters) {
        boolean value = true;
        for (String param : parameters) {
            if (param != null && !param.trim().isEmpty()) {
                value = false;
                break;
            }
        }
        return value;
    }

    // Validate the range (min <= max)
    private boolean isValidRange(String min, String max) {
        boolean isValid = true;

        if (!(min == null || min.trim().isEmpty() || max == null || max.trim().isEmpty())) {
            try {
                final int minValue = Integer.parseInt(min);
                final int maxValue = Integer.parseInt(max);
                isValid = minValue <= maxValue;
            }
            catch (NumberFormatException exception) {
                isValid = false;
            }
        }
        return isValid;
    }

    @Override
    public void switchToSearchResultsView() {
        recipePresenter.switchToResultsView();
    }

    @Override
    public void switchToProfileView() {
        recipePresenter.switchToProfileView();
    }
}
