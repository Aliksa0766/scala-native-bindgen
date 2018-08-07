#ifndef SCALA_NATIVE_BINDGEN_VARDEFINE_H
#define SCALA_NATIVE_BINDGEN_VARDEFINE_H

#include "Define.h"
#include "Variable.h"

/**
 * Stores a pointer to Variable instance from IR
 */
class VarDefine : public Define {
  public:
    VarDefine(std::string name, std::shared_ptr<Variable> variable);

    std::string getDefinition(const LocationManager &locationManager) const;

    friend llvm::raw_ostream &operator<<(llvm::raw_ostream &s,
                                         const VarDefine &varDefine);

    bool hasIllegalUsageOfOpaqueType() const;

  private:
    std::shared_ptr<Variable> variable;
};

#endif // SCALA_NATIVE_BINDGEN_VARDEFINE_H
