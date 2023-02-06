package com.dicoding.academy.themealsapp.app

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dicoding.academy.themealsapp.R
import com.dicoding.academy.themealsapp.core.domain.model.CategoryModel
import com.dicoding.academy.themealsapp.module.detail.DetailScreen
import com.dicoding.academy.themealsapp.module.favorite.FavoriteScreen
import com.dicoding.academy.themealsapp.module.home.HomeScreen
import com.dicoding.academy.themealsapp.module.profile.ProfileScreen
import com.dicoding.academy.themealsapp.ui.common.categoryJSONToModel
import com.dicoding.academy.themealsapp.ui.common.categoryModelToJSON
import com.dicoding.academy.themealsapp.ui.navigation.NavigationItem
import com.dicoding.academy.themealsapp.ui.navigation.Screen
import com.dicoding.academy.themealsapp.ui.theme.MyMovieTheme
import com.google.gson.Gson
import com.squareup.moshi.Moshi

@Composable
fun MealApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != Screen.DetailCategory.route) {
                BottomBar(navController)
            }
        },
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    navigateToDetail = {
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            key = "categoryModel",
                            value = it
                        )
                        navController.navigate(Screen.DetailCategory.route)
                    }
                )
            }
            composable(Screen.Favorite.route) {
                val context = LocalContext.current
                FavoriteScreen(
//                    onOrderButtonClicked = { message ->
//                        shareOrder(context, message)
//                    }
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen()
            }
            composable(Screen.DetailCategory.route) {
                val categoryModel = navController.previousBackStackEntry?.savedStateHandle?.get<CategoryModel>("categoryModel")
                if (categoryModel != null) {
                    Log.i("categoryModel", "$categoryModel")
                    DetailScreen(
                        categoryModel = categoryModel
                    )
                } else {
                    Log.i("categoryModel", "Null Bosque")
                }
            }
        }
    }
}

private fun shareOrder(context: Context, summary: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.detail))
        putExtra(Intent.EXTRA_TEXT, summary)
    }

    context.startActivity(
        Intent.createChooser(
            intent,
            context.getString(R.string.detail)
        )
    )
}


@Composable
private fun BottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    BottomNavigation(
        modifier = modifier
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val navigationItems = listOf(
            NavigationItem(
                title = stringResource(R.string.menu_home),
                icon = Icons.Default.Home,
                screen = Screen.Home
            ),
            NavigationItem(
                title = stringResource(R.string.menu_favorite),
                icon = Icons.Default.Favorite,
                screen = Screen.Favorite
            ),
            NavigationItem(
                title = stringResource(R.string.menu_profile),
                icon = Icons.Default.AccountCircle,
                screen = Screen.Profile
            ),
        )
        BottomNavigation {
            navigationItems.map { item ->
                BottomNavigationItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title
                        )
                    },
                    label = { Text(item.title) },
                    selected = currentRoute == item.screen.route,
                    onClick = {
                        navController.navigate(item.screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            restoreState = true
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyMovieTheme {
        MealApp()
    }
}