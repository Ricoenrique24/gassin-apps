package com.naffeid.gassin.ui.pages

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.naffeid.gassin.data.repository.AuthRepository
import com.naffeid.gassin.data.repository.CustomerRepository
import com.naffeid.gassin.data.repository.EmployeeRepository
import com.naffeid.gassin.data.repository.PurchaseTransactionRepository
import com.naffeid.gassin.data.repository.StoreRepository
import com.naffeid.gassin.data.repository.UserRepository
import com.naffeid.gassin.di.Injection
import com.naffeid.gassin.ui.pages.employee.main.home.HomeEmployeeViewModel
import com.naffeid.gassin.ui.pages.employee.main.more.MoreEmployeeViewModel
import com.naffeid.gassin.ui.pages.signin.SignInViewModel
import com.naffeid.gassin.ui.pages.splashscreen.SplashViewModel
import com.naffeid.gassin.ui.pages.manager.choose.customer.ChooseCustomerViewModel as ChooseCustomerManagerViewModel
import com.naffeid.gassin.ui.pages.manager.choose.employee.ChooseEmployeeViewModel as ChooseEmployeeManagerViewModel
import com.naffeid.gassin.ui.pages.manager.choose.store.ChooseStoreViewModel as ChooseStoreManagerViewModel
import com.naffeid.gassin.ui.pages.manager.customer.create.CreateCustomerViewModel as CreateCustomerManagerViewModel
import com.naffeid.gassin.ui.pages.manager.customer.edit.EditCustomerViewModel as EditCustomerManagerViewModel
import com.naffeid.gassin.ui.pages.manager.customer.index.IndexCustomerViewModel as IndexCustomerManagerViewModel
import com.naffeid.gassin.ui.pages.manager.customer.show.ShowCustomerViewModel as ShowCustomerManagerViewModel
import com.naffeid.gassin.ui.pages.manager.employee.create.CreateEmployeeViewModel as CreateEmployeeManagerViewModel
import com.naffeid.gassin.ui.pages.manager.employee.edit.EditEmployeeViewModel as EditEmployeeManagerViewModel
import com.naffeid.gassin.ui.pages.manager.employee.index.IndexEmployeeViewModel as IndexEmployeeManagerViewModel
import com.naffeid.gassin.ui.pages.manager.employee.show.ShowEmployeeViewModel as ShowEmployeeManagerViewModel
import com.naffeid.gassin.ui.pages.manager.main.home.HomeViewModel as HomeManagerViewModel
import com.naffeid.gassin.ui.pages.manager.main.more.MoreViewModel as MoreManagerViewModel
import com.naffeid.gassin.ui.pages.manager.purchasetransaction.create.CreatePurchaseTransactionViewModel as CreatePurchaseTransactionManagerViewModel
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
    private val purchaseTransactionRepository: PurchaseTransactionRepository
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
        } else if (modelClass.isAssignableFrom(IndexEmployeeManagerViewModel::class.java)) {
            return IndexEmployeeManagerViewModel(employeeRepository) as T
        } else if (modelClass.isAssignableFrom(CreateEmployeeManagerViewModel::class.java)) {
            return CreateEmployeeManagerViewModel(employeeRepository) as T
        } else if (modelClass.isAssignableFrom(ShowEmployeeManagerViewModel::class.java)) {
            return ShowEmployeeManagerViewModel(employeeRepository) as T
        } else if (modelClass.isAssignableFrom(EditEmployeeManagerViewModel::class.java)) {
            return EditEmployeeManagerViewModel(employeeRepository) as T
        } else if (modelClass.isAssignableFrom(CreatePurchaseTransactionManagerViewModel::class.java)) {
            return CreatePurchaseTransactionManagerViewModel(purchaseTransactionRepository,customerRepository,employeeRepository) as T
        } else if (modelClass.isAssignableFrom(ChooseCustomerManagerViewModel::class.java)) {
            return ChooseCustomerManagerViewModel(customerRepository) as T
        } else if (modelClass.isAssignableFrom(ChooseEmployeeManagerViewModel::class.java)) {
            return ChooseEmployeeManagerViewModel(employeeRepository) as T
        } else if (modelClass.isAssignableFrom(ChooseStoreManagerViewModel::class.java)) {
            return ChooseStoreManagerViewModel(storeRepository) as T
        } else if (modelClass.isAssignableFrom(HomeEmployeeViewModel::class.java)) {
            return HomeEmployeeViewModel(userRepository) as T
        } else if (modelClass.isAssignableFrom(MoreEmployeeViewModel::class.java)) {
            return MoreEmployeeViewModel(userRepository) as T
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
                Injection.providePurchaseTransactionRepository(context)
            )
        }
    }
}