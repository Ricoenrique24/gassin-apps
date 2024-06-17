package com.naffeid.gassin.ui.pages

//Manager ViewModel
//Main Manager ViewModel
//Store ViewModel
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.naffeid.gassin.data.repository.AuthRepository
import com.naffeid.gassin.data.repository.CustomerRepository
import com.naffeid.gassin.data.repository.EmployeeRepository
import com.naffeid.gassin.data.repository.StoreRepository
import com.naffeid.gassin.data.repository.UserRepository
import com.naffeid.gassin.di.Injection
import com.naffeid.gassin.ui.pages.signin.SignInViewModel
import com.naffeid.gassin.ui.pages.splashscreen.SplashViewModel
import com.naffeid.gassin.ui.pages.manager.customer.create.CreateCustomerViewModel as CreateCustomerManagerViewModel
import com.naffeid.gassin.ui.pages.manager.customer.edit.EditCustomerViewModel as EditCustomerManagerViewModel
import com.naffeid.gassin.ui.pages.manager.customer.index.IndexCustomerViewModel as IndexCustomerManagerViewModel
import com.naffeid.gassin.ui.pages.manager.customer.show.ShowCustomerViewModel as ShowCustomerManagerViewModel
import com.naffeid.gassin.ui.pages.manager.main.home.HomeViewModel as HomeManagerViewModel
import com.naffeid.gassin.ui.pages.manager.main.more.MoreViewModel as MoreManagerViewModel
import com.naffeid.gassin.ui.pages.manager.store.create.CreateStoreViewModel as CreateStoreManagerViewModel
import com.naffeid.gassin.ui.pages.manager.store.edit.EditStoreViewModel as EditStoreManagerViewModel
import com.naffeid.gassin.ui.pages.manager.store.index.IndexStoreViewModel as IndexStoreManagerViewModel
import com.naffeid.gassin.ui.pages.manager.store.show.ShowStoreViewModel as ShowStoreManagerViewModel

class ViewModelFactory private constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val employeeRepository: EmployeeRepository,
    private val storeRepository: StoreRepository,
    private val customerRepository: CustomerRepository,
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(userRepository) as T
        } else if (modelClass.isAssignableFrom(SignInViewModel::class.java)) {
            return SignInViewModel(userRepository, authRepository) as T
        } else if (modelClass.isAssignableFrom(HomeManagerViewModel::class.java)) {
            return HomeManagerViewModel(userRepository) as T
        } else if (modelClass.isAssignableFrom(MoreManagerViewModel::class.java)) {
            return MoreManagerViewModel(userRepository) as T
        } else if (modelClass.isAssignableFrom(IndexStoreManagerViewModel::class.java)) {
            return IndexStoreManagerViewModel(storeRepository) as T
        } else if (modelClass.isAssignableFrom(CreateStoreManagerViewModel::class.java)) {
            return CreateStoreManagerViewModel(storeRepository) as T
        } else if (modelClass.isAssignableFrom(ShowStoreManagerViewModel::class.java)) {
            return ShowStoreManagerViewModel(storeRepository) as T
        } else if (modelClass.isAssignableFrom(EditStoreManagerViewModel::class.java)) {
            return EditStoreManagerViewModel(storeRepository) as T
        } else if (modelClass.isAssignableFrom(IndexCustomerManagerViewModel::class.java)) {
            return IndexCustomerManagerViewModel(customerRepository) as T
        } else if (modelClass.isAssignableFrom(CreateCustomerManagerViewModel::class.java)) {
            return CreateCustomerManagerViewModel(customerRepository) as T
        } else if (modelClass.isAssignableFrom(ShowCustomerManagerViewModel::class.java)) {
            return ShowCustomerManagerViewModel(customerRepository) as T
        } else if (modelClass.isAssignableFrom(EditCustomerManagerViewModel::class.java)) {
            return EditCustomerManagerViewModel(customerRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        fun getInstance(context: Context): ViewModelFactory {
            return ViewModelFactory(
                Injection.provideAuthRepository(),
                Injection.provideUserRepository(context),
                Injection.provideEmployeeRepository(context),
                Injection.provideStoreRepository(context),
                Injection.provideCustomerRepository(context),
            )
        }
    }
}