native = peripheral.native or peripheral
local native = native

local function decode(side)
	local lside, color = side:match("(%w%w*):(%w%w*)")
	
	if lside then
		return lside, color
	end
	
	return side
end

-- This can remain the same
wrap = native.wrap

function isPresent(side)
	local color
	side, color = decode(side)
	
	if not native.isPresent(side) then 
		return false
	end
	
	if color then 
		if native.getType(side) ~= "cable" then
			return false
		end
		return native.call(side, "isPresent", color)
	end
	
	return true
end

function getType(side)
	local color
	side, color = decode(side)
	
	if color then 
		if native.getType(side) ~= "cable" then
			return nil
		end
		return native.call(side, "getType", color)
	end
	
	return native.getType(side)
end

function getMethods(side)
	local color
	side, color = decode(side)
	
	if color then 
		if native.getType(side) ~= "cable" then
			return nil
		end
		return native.call(side, "getMethods", color)
	end
	
	return native.getMethods(side)
end

function call(side, method, ...)
	local color
	side, color = decode(side)
	
	if color then 
		if native.getType(side) ~= "cable" then
			error("No peripheral attached")
		end
		return native.call(side, "call", color, method, ...)
	end
	
	return native.call(side, method, ...)
end

function getSides()
	local sides = rs.getSides()
	
	for i,side in ipairs(rs.getSides()) do
		if native.getType(side) == "cable" then
			local lsides = { native.call(side, "list") }
			for _,ls in ipairs(lsides) do
				table.insert(sides, i, ls)
			end
		end
	end
	
	return sides
end