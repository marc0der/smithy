// When this model is queried by the NullableIndex, it should accurately
// report the intended nullability of each member because members are sent
// through the model upgrade process to add synthetic box traits.
$version: "1.0"

namespace smithy.example

structure Foo {
    nullable1: Boolean,
    nullable2: Integer,
    nullable3: MyBoolean,
    nullable4: String,

    @box
    nullable5: MyBoolean,
    nullable6: MyStruct,

    // because this was defined in 1.0, it's still nullable because the required trait wasn't
    // use in 1.0 nullability rules.
    @required
    nullable7: MyBoolean,

    nonNullable1: PrimitiveInteger,
    nonNullable2: PrimitiveBoolean,
    nonNullable3: MyPrimitiveBoolean
}

@box
boolean MyBoolean

boolean MyPrimitiveBoolean

structure MyStruct {}
