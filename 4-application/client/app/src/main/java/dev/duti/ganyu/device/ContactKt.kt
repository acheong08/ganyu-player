//Generated by the protocol buffer compiler. DO NOT EDIT!
// source: device.proto

package dev.duti.ganyu.device;

@kotlin.jvm.JvmSynthetic
public inline fun contact(block: dev.duti.ganyu.device.ContactKt.Dsl.() -> kotlin.Unit): dev.duti.ganyu.device.Contact =
  dev.duti.ganyu.device.ContactKt.Dsl._create(dev.duti.ganyu.device.Contact.newBuilder()).apply { block() }._build()
public object ContactKt {
  @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
  @com.google.protobuf.kotlin.ProtoDslMarker
  public class Dsl private constructor(
    private val _builder: dev.duti.ganyu.device.Contact.Builder
  ) {
    public companion object {
      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _create(builder: dev.duti.ganyu.device.Contact.Builder): Dsl = Dsl(builder)
    }

    @kotlin.jvm.JvmSynthetic
    @kotlin.PublishedApi
    internal fun _build(): dev.duti.ganyu.device.Contact = _builder.build()

    /**
     * <code>string phone_number = 1;</code>
     */
    public var phoneNumber: kotlin.String
      @JvmName("getPhoneNumber")
      get() = _builder.getPhoneNumber()
      @JvmName("setPhoneNumber")
      set(value) {
        _builder.setPhoneNumber(value)
      }
    /**
     * <code>string phone_number = 1;</code>
     */
    public fun clearPhoneNumber() {
      _builder.clearPhoneNumber()
    }

    /**
     * <code>string name = 2;</code>
     */
    public var name: kotlin.String
      @JvmName("getName")
      get() = _builder.getName()
      @JvmName("setName")
      set(value) {
        _builder.setName(value)
      }
    /**
     * <code>string name = 2;</code>
     */
    public fun clearName() {
      _builder.clearName()
    }
  }
}
@kotlin.jvm.JvmSynthetic
public inline fun dev.duti.ganyu.device.Contact.copy(block: dev.duti.ganyu.device.ContactKt.Dsl.() -> kotlin.Unit): dev.duti.ganyu.device.Contact =
  dev.duti.ganyu.device.ContactKt.Dsl._create(this.toBuilder()).apply { block() }._build()
