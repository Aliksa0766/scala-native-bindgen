#include "TypeDef.h"
#include "../Utils.h"
#include "Enum.h"
#include "Struct.h"
#include "Union.h"
#include <stdexcept>

TypeDef::TypeDef(std::string name, std::shared_ptr<const Type> type,
                 std::shared_ptr<Location> location)
    : TypeAndName(std::move(name), std::move(type)),
      LocatableType(std::move(location)) {}

llvm::raw_ostream &operator<<(llvm::raw_ostream &s, const TypeDef &typeDef) {
    s << "  type " << handleReservedWords(typeDef.name) << " = ";
    if (typeDef.type) {
        s << typeDef.getType()->str();
    } else {
        s << "native.CStruct0 // incomplete type";
    }
    s << "\n";
    return s;
}

bool TypeDef::usesType(
    const std::shared_ptr<const Type> &type, bool stopOnTypeDefs,
    std::vector<std::shared_ptr<const Type>> &visitedTypes) const {
    if (stopOnTypeDefs) {
        return false;
    }
    if (!this->type) {
        return false;
    }
    if (contains(this, visitedTypes)) {
        return false;
    }
    visitedTypes.push_back(shared_from_this());
    bool result = *this->type == *type ||
                  this->type->usesType(type, stopOnTypeDefs, visitedTypes);
    visitedTypes.pop_back();
    return result;
}

std::string TypeDef::str() const { return handleReservedWords(name); }

bool TypeDef::operator==(const Type &other) const {
    if (this == &other) {
        return true;
    }
    if (isInstanceOf<TypeDef>(&other)) {
        auto *typDef = dynamic_cast<const TypeDef *>(&other);
        if (name != typDef->name) {
            return false;
        }
        return *type == *typDef->type;
    }
    return false;
}

std::shared_ptr<Location> TypeDef::getLocation() const {
    if (!type) {
        throw std::logic_error(
            "TypeDef::getLocation method should not be called on typedef that "
            "references an opaque type because location cannot be "
            "defined.");
    }
    if (location) {
        /* if typedef is not generated */
        return location;
    }
    auto recordPointer = std::dynamic_pointer_cast<const Record>(type);
    if (recordPointer) {
        return recordPointer->getLocation();
    }
    auto enumPointer = std::dynamic_pointer_cast<const Enum>(type);
    if (enumPointer) {
        return enumPointer->getLocation();
    }
    throw std::logic_error(
        "Generated typedef may reference only struct, union or enum");
}

bool TypeDef::hasLocation() const { return location || type; }

bool TypeDef::findAllCycles(
    const std::shared_ptr<const Struct> &startStruct, CycleNode &cycleNode,
    std::vector<std::shared_ptr<const Type>> &visitedTypes) const {
    if (contains(this, visitedTypes) || !type) {
        return false;
    }
    visitedTypes.push_back(shared_from_this());
    bool result = type->findAllCycles(startStruct, cycleNode, visitedTypes);
    visitedTypes.pop_back();
    return result;
}

std::shared_ptr<const Type> TypeDef::unrollTypedefs() const {
    if (!type || isInstanceOf<Struct>(type.get()) ||
        isInstanceOf<Union>(type.get()) || isInstanceOf<Enum>(type.get())) {
        return std::make_shared<TypeDef>(name, type, nullptr);
    }
    return type->unrollTypedefs();
}

std::shared_ptr<const Type>
TypeDef::replaceType(const std::shared_ptr<const Type> &type,
                     const std::shared_ptr<const Type> &replacement) const {
    if (!this->type || isInstanceOf<Struct>(this->type.get()) ||
        isInstanceOf<Union>(this->type.get()) ||
        isInstanceOf<Enum>(this->type.get())) {
        return std::make_shared<TypeDef>(name, this->type, nullptr);
    }
    if (*this->type == *type) {
        return std::make_shared<TypeDef>(name, replacement, nullptr);
    }
    return std::make_shared<TypeDef>(
        name, this->type->replaceType(type, replacement), nullptr);
}
