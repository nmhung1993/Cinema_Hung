package com.apitiny.administrator.cinema_hung.ui.list

import com.apitiny.administrator.cinema_hung.ui.base.BaseContract
import com.apitiny.administrator.cinema_hung.models.DetailsViewModel
import com.apitiny.administrator.cinema_hung.models.Post

class ListContract {

    interface View: BaseContract.View {
        fun showProgress(show: Boolean)
        fun showErrorMessage(error: String)
        fun loadDataSuccess(list: List<Post>)
        fun loadDataAllSuccess(model: DetailsViewModel)
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun loadData()
        fun loadDataAll()
        fun deleteItem(item: Post)
    }
}