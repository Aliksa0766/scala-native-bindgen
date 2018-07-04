#include "TypeDef.h"
#include "../Utils.h"

TypeDef::TypeDef(std::string name, std::shared_ptr<Type> type)
    : TypeAndName(std::move(name), std::move(type)) {}

llvm::raw_ostream &operator<<(llvm::raw_ostream &s, const TypeDef &typeDef) {
    if (!typeDef.getType()) {
        llvm::errs() << "Error: type declaration for " << typeDef.getName()
                     << " was not found.\n";
        llvm::errs().flush();
        return s;
    }
    s << "  type " + handleReservedWords(typeDef.name) + " = " +
             typeDef.getType()->str() + "\n";
    return s;
}

bool TypeDef::usesType(std::shared_ptr<Type> type) const {
    return this == type.get() || this->type == type;
}

std::string TypeDef::str() const { return handleReservedWords(name); }
