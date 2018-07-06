#include "ArrayType.h"
#include "../../Utils.h"

ArrayType::ArrayType(std::shared_ptr<Type> elementsType, uint64_t size)
    : size(size), elementsType(std::move(elementsType)) {}

std::string ArrayType::str() const {
    return "native.CArray[" + elementsType->str() + ", " +
           uint64ToScalaNat(size) + "]";
}

bool ArrayType::usesType(const std::shared_ptr<Type> &type) const {
    return *elementsType == *type;
}

bool ArrayType::operator==(const Type &other) const {
    if (this == &other) {
        return true;
    }
    if (isInstanceOf<const ArrayType>(&other)) {
        auto *arrayType = dynamic_cast<const ArrayType *>(&other);
        if (size != arrayType->size) {
            return false;
        }
        return *elementsType == *arrayType->elementsType.get();
    }
    return false;
}
