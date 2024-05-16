package cn.li.network.api.user

import cn.li.network.dto.ApiResult
import cn.li.network.dto.user.CategoryDTO
import cn.li.network.dto.user.DishDTO
import cn.li.network.dto.user.DishSetMealDTO
import cn.li.network.dto.user.SetMealDTO
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 套餐浏览相关接口
 * */
internal interface UserDishApi {

    /**
     * 根据套餐id查询包含的商品列表
     * @param id 套餐id
     *
     * See: [文档](http://8.134.200.196:8080/doc.html#/user/%E5%A5%97%E9%A4%90%E6%B5%8F%E8%A7%88%E7%9B%B8%E5%85%B3%E6%8E%A5%E5%8F%A3/dishList)
     * */
    @GET("user/setmeal/dish/{id}")
    suspend fun getDishesListBySetMealId(@Path("id") id: Long): ApiResult<List<DishSetMealDTO>>

    /**
     * 根据分类id查询套餐
     * @param categoryId 分类id
     * see: [文旦](http://8.134.200.196:8080/doc.html#/user/%E5%A5%97%E9%A4%90%E6%B5%8F%E8%A7%88%E7%9B%B8%E5%85%B3%E6%8E%A5%E5%8F%A3/list_2)
     * */
    @GET("user/setmeal/list")
    suspend fun getSetMealListByCategoryId(@Query("categoryId") categoryId: Long): ApiResult<SetMealDTO>

    /**
     * 分类接口
     * @param shopId 商店的id
     * see: [文档](http://8.134.200.196:8080/doc.html#/user/%E5%88%86%E7%B1%BB%E6%8E%A5%E5%8F%A3/list_4)
     */
    @GET("user/category/list")
    suspend fun getCategoryList(@Query("shopId") shopId: String): ApiResult<List<CategoryDTO>>

    /**
     * 根据分类id获取菜品列表
     * @param id 分类id
     * see: [文档](http://8.134.200.196:8080/doc.html#/user/%E8%8F%9C%E5%93%81%E7%9B%B8%E5%85%B3%E6%8E%A5%E5%8F%A3/list_3)
     */
    @GET("user/dish/list")
    suspend fun getDishesListByCategoryId(@Query("categoryId") id: Long): ApiResult<List<DishDTO>>
}