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

PLATFORM=../../training/platform/target/platform
SRC="$(PLATFORM)/tomcat/webapps/alfresco/WEB-INF/lib"

i18n.lst:
	@echo "generating list of localized files ($@)"
	@(ls $(SRC)/*.jar | while read jar; do unzip -qql "$$jar" | awk '{print $$4}'; done \
		&& echo "alfresco/messages/tenant-interpreter-help_en.txt" \
		&& echo "alfresco/messages/workflow-interpreter-help_en.txt" \
		&& echo "alfresco/messages/repoadmin-interpreter-help_en.txt" \
	) \
		| grep -E '^alfresco/' \
		| grep -vE '/dbscripts/|thumbnail_placeholder' \
		| grep -E '_[a-z][a-z](_[A-Z][A-Z])?\.[a-z]+' \
		| perl -anp -e 's@_[a-z]{2}(_[A-Z]{2})?\.@_XX.@' \
		| sort -u \
		> $@

