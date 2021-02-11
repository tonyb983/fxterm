package io.imtony.vdrive.fxterm.utils

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

/**
 * Defines a mapping from one type to another.
 */
typealias Mapper<TInput, TOutput> = (TInput) -> TOutput

/**
 * Defines a function that supplies the target value.
 */
typealias TargetParameterSupplier<TOutput> = () -> TOutput

/**
 * Mapper that can convert one data class into another data class.
 * [TInput] defines the input type, [TOutput] defines the resulting type.
 */
class DataClassMapper<TInput : Any, TOutput : Any>(
  private val inType: KClass<TInput>,
  private val outType: KClass<TOutput>
) : Mapper<TInput, TOutput> {

  companion object {
    /**
     * Creates an artificial constructor using reified type parameters.
     * [TIn] defines the input type, [TOut] defines the resulting type.
     */
    inline operator fun <reified TIn : Any, reified TOut : Any> invoke(): DataClassMapper<TIn, TOut> =
      DataClassMapper(TIn::class, TOut::class)

    /**
     * Creates a mapper that converts a [Set] of [TIn] into a [Set] of [TOut].
     * [TIn] defines the input type, [TOut] defines the resulting type.
     */
    fun <TIn : Any, TOut : Any> setMapper(
      mapper: Mapper<TIn, TOut>
    ): Mapper<Set<TIn>, Set<TOut>> = object : Mapper<Set<TIn>, Set<TOut>> {
      override fun invoke(data: Set<TIn>): Set<TOut> = data.map(mapper).toSet()
    }
  }

  /**
   *
   */
  val fieldMappers: MutableMap<String, Mapper<Any, Any>> = mutableMapOf()
  private val targetParameterProviders: MutableMap<String, TargetParameterSupplier<Any>> = mutableMapOf()

  private val outConstructor: KFunction<TOutput> = outType.primaryConstructor!!
  private val inPropertiesByName: Map<String, KProperty1<TInput, *>> by lazy {
    inType.memberProperties.associateBy { it.name }
  }

  private fun argFor(parameter: KParameter, data: TInput): Any? {
    // get value from input data or apply a default value to the target class
    val value = inPropertiesByName[parameter.name]?.get(data)
      ?: return targetParameterProviders[parameter.name]?.invoke()

    // if a special mapper is registered, use it, otherwise keep value
    return fieldMappers[parameter.name]?.invoke(value) ?: value
  }

  /**
   * Registers a [mapper] for the given [parameterName].
   */
  inline fun <reified TIn : Any, reified TOut : Any> register(
    parameterName: String,
    crossinline mapper: Mapper<TIn, TOut>
  ): DataClassMapper<TInput, TOutput> = apply {
    this.fieldMappers[parameterName] = object : Mapper<Any, Any> {
      override fun invoke(data: Any): Any = mapper.invoke(data as TIn)
    }
  }

  /**
   * Registers a [mapper] for the [property] indicated.
   */
  inline fun <reified TOwner : Any, reified TIn : Any, reified TOut : Any> register(
    property: KProperty1<TOwner, TIn>,
    crossinline mapper: Mapper<TIn, TOut>
  ): DataClassMapper<TInput, TOutput> = apply {
    this.fieldMappers[property.name] = object : Mapper<Any, Any> {
      override fun invoke(data: Any): Any = mapper.invoke(data as TIn)
    }
  }

  /**
   * Creates a [TargetParameterSupplier] for the given [parameterName] using the given [supplier].
   */
  fun <TParam : Any> targetParameterSupplier(
    parameterName: String,
    supplier: TargetParameterSupplier<TParam>
  ): DataClassMapper<TInput, TOutput> = apply {
    this.targetParameterProviders[parameterName] = object : TargetParameterSupplier<Any> {
      override fun invoke(): Any = supplier.invoke()
    }
  }

  /**
   * Creates a [TargetParameterSupplier] for the target [property] using the given [supplier].
   */
  fun <S : Any, T : Any> targetParameterSupplier(
    property: KProperty1<S, Any?>,
    supplier: TargetParameterSupplier<T>
  ): DataClassMapper<TInput, TOutput> = apply {
    this.targetParameterProviders[property.name] = object : TargetParameterSupplier<Any> {
      override fun invoke(): Any = supplier.invoke()
    }
  }

  /**
   *
   */
  override fun invoke(data: TInput): TOutput = with(outConstructor) {
    callBy(parameters.associateWith { argFor(it, data) })
  }

  override fun toString(): String = "DataClassMapper($inType -> $outType)"

}
