## Makefile for checking which translation files are unused and which are missing.

all: check rcheck

check: i18n.lst
	@echo "checking for unknown translation files"
	@(cd src/main/resources && find alfresco -type f) \
		| grep -E '_(fi|sv)(_[A-Z][A-Z])?\.' \
		| while read f; do \
			g="$$(echo $$f | perl -anp -e 's@_(fi|sv)(_[A-Z]{2})?\.@_XX.@')"; \
			grep -q "$$g" i18n.lst || echo "? src/main/resources/$$f"; \
			done

rcheck: i18n.lst
	@echo "checking for missing translation files"
	@cat i18n.lst | while read f; do \
		g="src/main/resources/$${f/_XX/_fi}"; \
		test -f "$$g" || echo "! $$g"; \
		g="src/main/resources/$${f/_XX/_sv}"; \
		test -f "$$g" || echo "! $$g"; \
		done

.PHONY: all check rcheck 

PLATFORM=../../platform/target/platform
SRC="$(PLATFORM)/tomcat/webapps/share/WEB-INF/lib"

i18n.lst:
	@echo "generating list of localized files ($@)"
	@(ls $(SRC)/*.jar | while read jar; do unzip -qql "$$jar" | awk '{print $$4}'; done) \
		| grep -E '^(alfresco/|META-INF/js/aikau/)' \
		| grep -E '_[a-z][a-z](_[A-Z][A-Z])?\.[a-z]+' \
		| perl -anp -e 's@_[a-z]{2}(_[A-Z]{2})?\.@_XX.@' \
		| sort -u \
		> $@

i18n.properties:
	@echo "collecting all localizable strings ($@)"
	@rm -f $@
	@ls $(SRC)/*.jar | while read jar; do \
		FILES=$$(unzip -qql "$$jar" | awk '{print $$4}' \
			| grep -E '^(alfresco/|META-INF/js/aikau/)' \
			| grep -E '_[a-z][a-z](_[A-Z][A-Z])?\.[a-z]+' \
			| perl -anp -e 's@^(.*)_[a-z]{2}(?:_[A-Z]{2})?(\..*)$$@$$1$$2\n$$1_en$$2@' \
			| sort -u); \
			test "$$FILES" = "" || (echo -e "\n# processing $$jar"; unzip -p "$$jar" $$FILES) >> $@; \
		done

i18n_fi.properties:
	@echo "collecting all localizable strings ($@)"
	@rm -f $@
	@ls $(SRC)/*.jar | while read jar; do \
		FILES=$$(unzip -qql "$$jar" | awk '{print $$4}' \
			| grep -E '^(alfresco/|META-INF/js/aikau/)' \
			| grep -E '_fi(_FI)?\.[a-z]+' \
			| sort -u); \
			test "$$FILES" = "" || (echo -e "\n# processing $$jar"; unzip -p "$$jar" $$FILES) >> $@; \
		done

%.keys: %.properties
	@cat $< | sed -e '/^#/ { /^# processing/ p; d;}' | grep -vE '^\s' | grep = | cut -d= -f1 | sort -u  > $@

keys: i18n.keys i18n_fi.keys
