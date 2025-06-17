package com.example.android_kotlin_quickstart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import okhttp3.internal.wait
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchAndSortTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testSearchFunctionality() {
        // Add a hotel for search testing
        composeTestRule.onNodeWithTag("HomeScreenPlusButton").performClick()
        composeTestRule.onNodeWithTag("hotelNameTextField").performTextInput("SearchHotelTest")
        composeTestRule.onNodeWithTag("addEditHotelButton").performClick()
        composeTestRule.waitUntil(2_000) {
            composeTestRule.onAllNodesWithText("SearchHotelTest").fetchSemanticsNodes().isNotEmpty()
        }

        // Test partial search
        composeTestRule.onNodeWithText("Search by Name").performTextClearance()
        composeTestRule.onNodeWithText("Search by Name").performTextInput("Search")
        composeTestRule.waitUntil(2_000) {
            composeTestRule.onAllNodesWithText("SearchHotelTest").fetchSemanticsNodes().isNotEmpty()
        }

        // Test that clearing search shows all results
        composeTestRule.onNodeWithText("Search by Name").performTextClearance()
        composeTestRule.waitUntil(2_000) {
            composeTestRule.onAllNodesWithText("SearchHotelTest").fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun testSortFunctionality() {
        // Add two hotels for sorting
        composeTestRule.onNodeWithTag("HomeScreenPlusButton").performClick()
        composeTestRule.onNodeWithTag("hotelNameTextField").performTextInput("AlphaHotel")
        composeTestRule.onNodeWithTag("addEditHotelButton").performClick()
        composeTestRule.waitUntil(2_000) {
            composeTestRule.onAllNodesWithText("AlphaHotel").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("HomeScreenPlusButton").performClick()
        composeTestRule.onNodeWithTag("hotelNameTextField").performTextInput("BetaHotel")
        composeTestRule.onNodeWithTag("addEditHotelButton").performClick()
        composeTestRule.waitUntil(2_000) {
            composeTestRule.onAllNodesWithText("BetaHotel").fetchSemanticsNodes().isNotEmpty()
        }

        // Check initial order (should be AlphaHotel then BetaHotel)
        val hotelNodes = composeTestRule.onAllNodesWithText("Hotel")
        hotelNodes[0].assertTextContains("AlphaHotel")
        hotelNodes[1].assertTextContains("BetaHotel")

        // Tap sort to reverse order
        composeTestRule.onNodeWithText("Sort by name").performClick()

        // Now BetaHotel should be first
        composeTestRule.waitForIdle()
        val hotelNodesAfterSort = composeTestRule.onAllNodesWithText("Hotel")
        hotelNodesAfterSort[0].assertTextContains("BetaHotel")
        hotelNodesAfterSort[1].assertTextContains("AlphaHotel")
    }
} 