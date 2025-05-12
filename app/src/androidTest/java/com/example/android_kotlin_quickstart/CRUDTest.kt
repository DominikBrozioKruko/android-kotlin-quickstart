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
class CRUDTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testCRUD() {
        //Go To addHotel screen
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("HomeScreenPlusButton").assertExists()
        composeTestRule.onNodeWithTag("HomeScreenPlusButton").performClick()

        //test whether add button is disabled
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("addEditHotelButton").assertExists()
        composeTestRule.onNodeWithTag("addEditHotelButton").assertIsNotEnabled()
        composeTestRule.onNodeWithTag("addEditHotelButton").assertTextContains("SAVE")

        //Write hotel name
        composeTestRule.onNodeWithTag("hotelNameTextField").assertExists()
        composeTestRule.onNodeWithTag("hotelNameTextField").performClick()
        composeTestRule.onNodeWithTag("hotelNameTextField").performTextInput("1hotelTest")


        //check if button is enabled
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("addEditHotelButton").assertIsEnabled()
        composeTestRule.onNodeWithTag("addEditHotelButton").performClick()

        //check if app got the new hotel in list
        composeTestRule.waitUntil(2_000) {
            composeTestRule.onAllNodesWithText("1hotelTest").fetchSemanticsNodes().isNotEmpty()
        }

        //Go to edit hotel by swipe
        composeTestRule.onNodeWithText("1hotelTest").assertExists()
        composeTestRule.onNodeWithText("1hotelTest").performTouchInput {
            swipe(
                start = Offset(0f, centerY),
                end = Offset(width.toFloat(), centerY),
                durationMillis = 300
            )
        }

        //Edit title
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("hotelNameTextField").assertExists()
        composeTestRule.onNodeWithTag("hotelNameTextField").performClick()
        composeTestRule.onNodeWithTag("hotelNameTextField").performTextClearance()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("hotelNameTextField").performTextInput("2hotelTest")

        //check if button is enabled
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("addEditHotelButton").assertIsEnabled()
        composeTestRule.onNodeWithTag("addEditHotelButton").performClick()

        //check if app got the new hotel in list
        composeTestRule.waitUntil(2_000) {
            composeTestRule.onAllNodesWithText("2hotelTest").fetchSemanticsNodes().isNotEmpty()
        }

        //Remove Hotel
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("2hotelTest").performTouchInput {
            swipe(
                start = Offset(x = centerX, y = centerY),
                end = Offset(x = 0f, y = centerY),
                durationMillis = 300
            )
        }
        composeTestRule.waitUntil(2_000) {
            composeTestRule.onAllNodesWithText("2hotelTest").fetchSemanticsNodes().isEmpty()
        }
        composeTestRule.onNodeWithText("2hotelTest").assertDoesNotExist()

    }
}