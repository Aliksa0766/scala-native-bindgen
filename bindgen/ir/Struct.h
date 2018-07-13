#ifndef SCALA_NATIVE_BINDGEN_STRUCT_H
#define SCALA_NATIVE_BINDGEN_STRUCT_H

#include "TypeAndName.h"
#include "TypeDef.h"
#include "types/ArrayType.h"
#include <string>
#include <vector>

#define SCALA_NATIVE_MAX_STRUCT_FIELDS 22

class Field : public TypeAndName {
  public:
    Field(std::string name, std::shared_ptr<Type> type);
};

class StructOrUnion {
  public:
    StructOrUnion(std::string name, std::vector<std::shared_ptr<Field>> fields,
                  std::shared_ptr<Location> location);

    virtual std::shared_ptr<TypeDef> generateTypeDef() = 0;

    virtual std::string generateHelperClass() const = 0;

    std::string getName() const;

    bool equals(const StructOrUnion &other) const;

    std::shared_ptr<Location> getLocation() const;

    virtual std::string getTypeAlias() const = 0;

  protected:
    std::string name;
    std::vector<std::shared_ptr<Field>> fields;
    std::shared_ptr<Location> location;
};

class Struct : public StructOrUnion,
               public Type,
               public std::enable_shared_from_this<Struct> {
  public:
    Struct(std::string name, std::vector<std::shared_ptr<Field>> fields,
           uint64_t typeSize, std::shared_ptr<Location> location);

    std::shared_ptr<TypeDef> generateTypeDef() override;

    std::string generateHelperClass() const override;

    std::string getTypeAlias() const override;

    /**
     * @return true if helper methods will be generated for this struct
     */
    bool hasHelperMethods() const;

    bool usesType(const std::shared_ptr<Type> &type,
                  bool stopOnTypeDefs) const override;

    std::string str() const override;

    bool operator==(const Type &other) const override;

    std::string generateSetter(unsigned fieldIndex) const;

    std::string generateGetter(unsigned fieldIndex) const;

  private:
    /* type size is needed if number of fields is bigger than 22 */
    uint64_t typeSize;
};

class Union : public StructOrUnion,
              public ArrayType,
              public std::enable_shared_from_this<Union> {
  public:
    Union(std::string name, std::vector<std::shared_ptr<Field>> fields,
          uint64_t maxSize, std::shared_ptr<Location> location);

    std::shared_ptr<TypeDef> generateTypeDef() override;

    std::string generateHelperClass() const override;

    bool operator==(const Type &other) const override;

    std::string getTypeAlias() const override;
};

#endif // SCALA_NATIVE_BINDGEN_STRUCT_H
